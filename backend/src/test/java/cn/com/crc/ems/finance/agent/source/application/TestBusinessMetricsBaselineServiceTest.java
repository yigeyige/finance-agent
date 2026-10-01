package cn.com.crc.ems.finance.agent.source.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 *
 * @Description: 验证测试业务样本量测基线不会虚构运行时数据或改写非确认状态。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
class TestBusinessMetricsBaselineServiceTest {

    @Test
    void calculatesOnlyDeterministicSampleMetrics() {
        var service = new TestBusinessMetricsBaselineService(
                new TestBusinessPilotPreflightService(new TestBusinessReferenceSampleCatalog()));

        var result = service.calculate();

        assertThat(result.referenceSampleVersion()).isEqualTo(TestBusinessReferenceSampleCatalog.VERSION);
        assertThat(result.factPrecision()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(result.factRecall()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(result.classificationAccuracy()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(result.unknownRate()).isEqualByComparingTo("0.25");
        assertThat(result.coverageRate()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(result.manualConfirmations()).isEqualTo(3);
    }
}
