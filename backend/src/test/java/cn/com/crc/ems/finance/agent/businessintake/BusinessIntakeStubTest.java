package cn.com.crc.ems.finance.agent.businessintake;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeOption;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.infrastructure.StubBusinessModelClient;

/**
 * @Description: 验证 Stub 模式的确定性预填结果与候选名称匹配。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 16:10:00
 */
class BusinessIntakeStubTest {

    @Test
    void returnsDeterministicResultAndMatchesMentionedCandidates() {
        StubBusinessModelClient client = new StubBusinessModelClient();
        BusinessIntakeContext context = new BusinessIntakeContext("需要核查费用报销核查场景在财务主库中的数据",
                List.of(new BusinessIntakeOption(UUID.randomUUID().toString(), "费用报销核查")),
                List.of(new BusinessIntakeOption(UUID.randomUUID().toString(), "财务主库")));

        BusinessModelResult<BusinessIntakeSuggestion> result = client.extractBusinessIntake(context);

        assertThat(result.value().title()).isNotBlank();
        assertThat(result.value().description()).isEqualTo(context.text());
        assertThat(result.value().sceneSkillName()).isEqualTo("费用报销核查");
        assertThat(result.value().databaseName()).isEqualTo("财务主库");
        assertThat(result.metadata().mode()).isEqualTo("STUB");
        assertThat(result.metadata().tokens()).isZero();
    }
}
