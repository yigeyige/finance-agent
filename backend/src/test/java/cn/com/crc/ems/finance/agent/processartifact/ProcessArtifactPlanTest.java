package cn.com.crc.ems.finance.agent.processartifact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactPlan;
import cn.com.crc.ems.finance.agent.processartifact.domain.EmsProcessArtifactConverter;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/** 验证受限流程中间表示与 EMS 导入文件的最低结构契约。 */
class ProcessArtifactPlanTest {

    private static final String SIMPLE = """
            {"name":"测试流程","nodes":[{"id":"draft","kind":"draft","name":"起草","assignee":""}],
             "flows":[{"source":"start","target":"draft","condition":null},
                      {"source":"draft","target":"end","condition":null}],
             "nodeParameters":[],"departmentApprovals":[],"approvalRules":[],
             "missingItems":[],"unsupported":[]}
            """;

    @Test
    void convertsMinimalFlowWithoutReusingOldProcessIdentifiers() {
        var plan = ProcessArtifactPlan.fromJson(CanonicalJsonCodec.parse(SIMPLE));

        var artifact = new EmsProcessArtifactConverter().convert(plan);
        var model = CanonicalJsonCodec.parse(artifact.modelJson());

        assertThat(artifact.fileName()).isEqualTo("测试流程BPMN.xml");
        assertThat(artifact.nodeParametersJson()).isNull();
        assertThat(artifact.departmentApprovalsJson()).isNull();
        assertThat(artifact.approvalRulesJson()).isNull();
        assertThat(model.path("properties").path("process_id").asText()).isEmpty();
        assertThat(model.path("childShapes")).hasSize(5);
        assertThat(model.toString()).contains("StartNoneEvent", "UserTask", "SequenceFlow", "EndNoneEvent");
    }

    @Test
    void rejectsBrokenReferencesAndParallelGateway() {
        String missingTarget = SIMPLE.replace("\"target\":\"end\"", "\"target\":\"missing\"");
        assertThatThrownBy(() -> ProcessArtifactPlan.fromJson(CanonicalJsonCodec.parse(missingTarget)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("流转");
        String parallel = SIMPLE.replace("\"kind\":\"draft\"", "\"kind\":\"parallel-gateway\"");
        assertThatThrownBy(() -> ProcessArtifactPlan.fromJson(CanonicalJsonCodec.parse(parallel)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("节点类型");
    }

    @Test
    void marksDisconnectedNodeForRepairAndRoundTripsPlan() {
        String withOrphan = SIMPLE.replace("\"assignee\":\"\"}",
                "\"assignee\":\"\"},{\"id\":\"review\",\"kind\":\"approve\",\"name\":\"审批\",\"assignee\":\"\"}");
        var plan = ProcessArtifactPlan.fromJson(CanonicalJsonCodec.parse(withOrphan));
        var roundTrip = ProcessArtifactPlan.fromJson(CanonicalJsonCodec.parse(
                CanonicalJsonCodec.canonicalize(plan.toJson())));

        assertThat(roundTrip.nodes()).hasSize(2);
        assertThat(new EmsProcessArtifactConverter().convert(roundTrip).importStatus()).isEqualTo("NEEDS_REPAIR");
    }

    @Test
    void configFilesKeepApplicableFieldsAndDropSourceIdentifiers() {
        String configured = SIMPLE.replace("\"nodeParameters\":[]",
                "\"nodeParameters\":[{\"id\":99,\"procdefId\":\"old\",\"actId\":\"draft\",\"variableName\":\"amount\"}]")
                .replace("\"departmentApprovals\":[]",
                        "\"departmentApprovals\":[{\"id\":77,\"processTemplateCode\":\"old\",\"approvalNode\":\"draft\"}]")
                .replace("\"approvalRules\":[]",
                        "\"approvalRules\":[{\"head\":{\"rulesHeadId\":1,\"processId\":\"old\",\"workflowLineKey\":\"amount\"},\"lineList\":[{\"rulesLineId\":2,\"rulesHeadId\":1,\"workflowLineValue\":\"A001\"}]}]");
        var artifact = new EmsProcessArtifactConverter().convert(
                ProcessArtifactPlan.fromJson(CanonicalJsonCodec.parse(configured)));

        assertThat(artifact.nodeParametersJson()).contains("\"procdefId\":\"\"").doesNotContain("old");
        assertThat(artifact.departmentApprovalsJson()).contains("\"processTemplateCode\":\"\"")
                .doesNotContain("old");
        assertThat(artifact.approvalRulesJson()).contains("\"rulesHeadId\":null", "\"processId\":\"\"")
                .doesNotContain("old");
    }

    @Test
    void incompleteApprovalRuleStillProducesEditableFile() {
        String incomplete = SIMPLE.replace("\"approvalRules\":[]", "\"approvalRules\":[{}]");
        var artifact = new EmsProcessArtifactConverter().convert(
                ProcessArtifactPlan.fromJson(CanonicalJsonCodec.parse(incomplete)));

        assertThat(artifact.importStatus()).isEqualTo("NEEDS_REPAIR");
        assertThat(artifact.approvalRulesJson()).contains("\"head\"", "\"lineList\":[]");
    }

    @Test
    void marksReversedApprovalAmountTierForRepair() {
        String reversed = SIMPLE.replace("\"approvalRules\":[]",
                "\"approvalRules\":[{\"head\":{},\"lineList\":[{\"applyAmountMin\":100,\"applyAmountMax\":10}]}]");
        var artifact = new EmsProcessArtifactConverter().convert(
                ProcessArtifactPlan.fromJson(CanonicalJsonCodec.parse(reversed)));

        assertThat(artifact.importStatus()).isEqualTo("NEEDS_REPAIR");
        assertThat(artifact.missingItems()).anyMatch(item -> item.contains("档位范围"));
    }
}
