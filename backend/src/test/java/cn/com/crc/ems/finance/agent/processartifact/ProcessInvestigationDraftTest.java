package cn.com.crc.ems.finance.agent.processartifact;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.BusinessHandlingDraft;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactPlan;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/** 验证场景结构化调查结论随草稿保存及重新读取，其他场景字段保持原状。 */
class ProcessInvestigationDraftTest {

    @Test
    void preservesOptionalProcessFactsAcrossDraftJson() {
        var plan = ProcessArtifactPlan.fromJson(CanonicalJsonCodec.parse("""
                {"name":"测试流程","nodes":[],"flows":[],"nodeParameters":[],
                 "departmentApprovals":[],"approvalRules":[],"missingItems":["待确认审批人"],"unsupported":[]}
                """));
        var draft = new BusinessHandlingDraft("流程", "流程", "推荐方案", List.of(), "依据", "调查结论", List.of(), plan);

        var restored = BusinessHandlingDraft.fromJson(CanonicalJsonCodec.parse(draft.toJson().toString()));

        assertThat(restored.processPlan()).isNotNull();
        assertThat(restored.processPlan().missingItems()).containsExactly("待确认审批人");
        assertThat(restored.toJson().path("processPlan").path("name").asText()).isEqualTo("测试流程");
    }
}
