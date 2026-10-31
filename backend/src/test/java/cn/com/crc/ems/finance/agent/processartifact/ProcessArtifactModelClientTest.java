package cn.com.crc.ems.finance.agent.processartifact;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.infrastructure.StubBusinessModelClient;
import cn.com.crc.ems.finance.agent.processartifact.application.ProcessArtifactModelContext;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/** 验证文件生成模型只接收固定决策包内容和场景提示词。 */
class ProcessArtifactModelClientTest {

    @Test
    void stubUsesPacketSnapshot() {
        var packet = CanonicalJsonCodec.parse("""
                {"recommendedGoal":"按调查结论生成", "guidanceDraft":{"processPlan":{
                "name":"测试流程","nodes":[],"flows":[],"nodeParameters":[],
                "departmentApprovals":[],"approvalRules":[],"missingItems":[],"unsupported":[]}},"evidence":[]}
                """);
        var context = new ProcessArtifactModelContext(UUID.randomUUID(), UUID.randomUUID(), 1,
                packet, "生成文件提示词");

        var result = new StubBusinessModelClient().generateProcessArtifacts(context);

        assertThat(result.value().name()).isEqualTo("测试流程");
        assertThat(result.metadata().mode()).isEqualTo("STUB");
    }
}
