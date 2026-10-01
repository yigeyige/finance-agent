package cn.com.crc.ems.finance.agent.source.application;

import java.math.BigDecimal;

/**
 *
 * @Description: 承载固定测试业务样本的量测基线，不包含运行时性能或成本数值。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
public record TestBusinessMetricsBaseline(String referenceSampleVersion, BigDecimal factPrecision, BigDecimal factRecall,
        BigDecimal classificationAccuracy, BigDecimal unknownRate, BigDecimal coverageRate, int manualConfirmations) {
}
