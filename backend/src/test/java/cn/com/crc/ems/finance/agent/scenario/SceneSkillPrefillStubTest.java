package cn.com.crc.ems.finance.agent.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillContext;
import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillTarget;
import cn.com.crc.ems.finance.agent.harness.infrastructure.StubBusinessModelClient;

/**
 * @Description: 验证 Stub 场景预填的确定性输出与目标语义。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 18:10:00
 */
class SceneSkillPrefillStubTest {

    @Test
    void returnsDeterministicContentPerTarget() {
        StubBusinessModelClient client = new StubBusinessModelClient();
        BusinessModelResult<SceneSkillPrefillSuggestion> investigation = client.extractSceneSkillPrefill(
                new SceneSkillPrefillContext(SceneSkillPrefillTarget.INVESTIGATION,
                        "核查费用报销与审批配置", "", "", List.of("basedata.employee_info")));
        assertThat(investigation.value().name()).endsWith("核查场景");
        assertThat(investigation.value().content()).contains("basedata.employee_info");
        assertThat(investigation.metadata().mode()).isEqualTo("STUB");

        BusinessModelResult<SceneSkillPrefillSuggestion> decision = client.extractSceneSkillPrefill(
                new SceneSkillPrefillContext(SceneSkillPrefillTarget.DECISION_PACKET,
                        "核查费用报销", "场景", "已有调查提示词", List.of()));
        assertThat(decision.value().name()).isEmpty();
        assertThat(decision.value().content()).isNotBlank();
    }
}
