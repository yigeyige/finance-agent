package cn.com.crc.ems.finance.agent.task.application;

import java.math.BigDecimal;

/**
 *
 * @Description: 任务预算上限及已消费快照。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record BudgetSnapshot(int maxSteps, int maxTokens, BigDecimal maxCost,
        int usedSteps, int usedTokens, BigDecimal usedCost) {

    public BudgetSnapshot {
        if (maxSteps < 1 || maxTokens < 1 || maxCost == null || maxCost.signum() < 0
                || usedSteps < 0 || usedTokens < 0 || usedCost == null || usedCost.signum() < 0) {
            throw new IllegalArgumentException("budget limits and usage are invalid");
        }
    }
}
