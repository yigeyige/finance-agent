package cn.com.crc.ems.finance.agent.harness.application;

import java.math.BigDecimal;
import java.time.Duration;

/** 单次结构化模型调用的有限预算。 */
public record ModelBudget(int maxSteps, int maxTokens, BigDecimal maxCost, Duration timeout) {

    public ModelBudget {
        if (maxSteps < 1 || maxTokens < 1 || maxCost == null || maxCost.signum() < 0
                || timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("model budget is invalid");
        }
    }
}
