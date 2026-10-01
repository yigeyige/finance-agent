package cn.com.crc.ems.finance.agent.source.application;

import java.math.BigDecimal;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 *
 * @Description: 仅在 local/test 环境从固定试点预检结果计算样本侧量测基线。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
@Service
@Profile({"local", "test"})
public class TestBusinessMetricsBaselineService {

    private final TestBusinessPilotPreflightService preflightService;

    public TestBusinessMetricsBaselineService(TestBusinessPilotPreflightService preflightService) {
        if (preflightService == null) {
            throw new IllegalArgumentException("test business pilot preflight service is required");
        }
        this.preflightService = preflightService;
    }

    /**
     *
     * @description: 计算固定黄金事实样本的准确率、召回率、未知率、覆盖和人工确认基线。
     *
     * @return: 只含样本侧指标的不可变基线，不含运行时性能或成本。
     * @author: yigeyige99
     */
    public TestBusinessMetricsBaseline calculate() {
        TestBusinessPilotPreflightResult preflight = preflightService.preflight();
        int total = preflight.coveredSamples();
        if (total == 0) {
            throw new IllegalStateException("test business pilot preflight has no samples");
        }
        BigDecimal totalValue = BigDecimal.valueOf(total);
        return new TestBusinessMetricsBaseline(preflight.referenceSampleVersion(), BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.valueOf(preflight.unknownSamples()).divide(totalValue), BigDecimal.ONE,
                preflight.expectedManualConfirmations());
    }
}
