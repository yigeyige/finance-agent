package cn.com.crc.ems.finance.agent.processartifact;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.BusinessHandlingDraft;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactPlan;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactPlanNormalizer;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/** 验证初稿生成阶段对模型流程结构偏差的规范化与降级行为。 */
class ProcessArtifactPlanNormalizerTest {

    @Test
    void normalizesCommonDeviationsAndKeepsFlowReferences() {
        String raw = """
                {"name":"测试/流程",
                 "nodes":[
                   {"id":"start","kind":"start","name":"开始","assignee":null},
                   {"id":"dept-leader","kind":"approval","name":"部门负责人","assignee":null},
                   {"id":"gateway","kind":"gateway","name":"角色判断","assignee":""}
                 ],
                 "flows":[
                   {"source":"start","target":"dept-leader","condition":null},
                   {"source":"dept-leader","target":"gateway","condition":{"variable":"roleCode","operator":"==","value":"ZYMD"}},
                   {"source":"gateway","target":"end","condition":{"variable":"角色","operator":"=","value":"中艺"}}
                 ],
                 "nodeParameters":[{"actId":"dept-leader","variableName":"approver"}],
                 "departmentApprovals":[{"approvalNode":"missing"}],
                 "approvalRules":[],"missingItems":["待确认审批人"],"unsupported":[]}
                """;

        ProcessArtifactPlanNormalizer.Result result = ProcessArtifactPlanNormalizer.normalize(
                CanonicalJsonCodec.parse(raw));

        assertThat(result.plan()).isNotNull();
        assertThat(result.plan().nodes()).extracting(ProcessArtifactPlan.Node::id)
                .containsExactly("dept_leader", "gateway");
        assertThat(result.plan().nodes().get(0).kind()).isEqualTo("approve");
        assertThat(result.plan().nodes().get(0).assignee()).isEmpty();
        assertThat(result.plan().nodes().get(1).kind()).isEqualTo("exclusive-gateway");
        assertThat(result.plan().flows()).hasSize(3);
        assertThat(result.plan().flows().get(1).source()).isEqualTo("dept_leader");
        assertThat(result.plan().flows().get(2).condition()).isNull();
        assertThat(result.plan().nodeParameters()).singleElement()
                .satisfies(item -> assertThat(item.path("actId").asText()).isEqualTo("dept_leader"));
        assertThat(result.plan().departmentApprovals()).isEmpty();
        assertThat(result.warnings()).isNotEmpty();
    }

    @Test
    void keepsDraftWhenProcessPlanCannotBeParsed() {
        String raw = """
                {"subjectKey":"测试流程","objectKey":"测试流程","recommendedGoal":"补充流程",
                 "alternativeGoals":[],"reason":"模型返回结构异常","conclusion":"需要补充核查",
                 "evidence":[],"processPlan":"not-an-object"}
                """;
        List<String> warnings = new ArrayList<>();

        BusinessHandlingDraft draft = BusinessHandlingDraft.fromJson(CanonicalJsonCodec.parse(raw), warnings);

        assertThat(draft.processPlan()).isNull();
        assertThat(draft.conclusion()).isEqualTo("需要补充核查");
        assertThat(warnings).isNotEmpty();
    }

    @Test
    void keepsStrictValidationForMissingDraftFields() {
        String raw = """
                {"subjectKey":"","objectKey":"测试流程","recommendedGoal":"补充流程",
                 "alternativeGoals":[],"reason":"依据","conclusion":"结论","evidence":[]}
                """;

        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> BusinessHandlingDraft.fromJson(CanonicalJsonCodec.parse(raw)))
                .isInstanceOf(cn.com.crc.ems.finance.agent.harness.application.BusinessModelOutputException.class);
    }
}
