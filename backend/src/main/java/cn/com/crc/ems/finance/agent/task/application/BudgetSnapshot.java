package cn.com.crc.ems.finance.agent.task.application;

import java.math.BigDecimal;

/** 任务预算上限及已消费快照。 */
public record BudgetSnapshot(int maxSteps, int maxTokens, BigDecimal maxCost,
        int usedSteps, int usedTokens, BigDecimal usedCost) {

    public BudgetSnapshot {
        if (maxSteps < 1 || maxTokens < 1 || maxCost == null || maxCost.signum() < 0
                || usedSteps < 0 || usedTokens < 0 || usedCost == null || usedCost.signum() < 0) {
            throw new IllegalArgumentException("budget limits and usage are invalid");
        }
    }
}
