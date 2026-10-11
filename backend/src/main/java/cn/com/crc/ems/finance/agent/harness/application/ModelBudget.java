package cn.com.crc.ems.finance.agent.harness.application;

import java.math.BigDecimal;
import java.time.Duration;

/**
 *
 * @Description: 单次结构化模型调用的有限预算。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ModelBudget(int maxSteps, int maxTokens, BigDecimal maxCost, Duration timeout) {

    public ModelBudget {
        if (maxSteps < 1 || maxTokens < 1 || maxCost == null || maxCost.signum() < 0
                || timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("model budget is invalid");
        }
    }
}
