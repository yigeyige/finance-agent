package cn.com.crc.ems.finance.agent.task.application;

import java.math.BigDecimal;

/** 一次模型消费的可审计预算增量。 */
public record BudgetUsage(int steps, int tokens, BigDecimal cost) {

    public BudgetUsage {
        if (steps < 0 || tokens < 0 || cost == null || cost.signum() < 0) {
            throw new IllegalArgumentException("budget usage must be non-negative");
        }
    }
}
