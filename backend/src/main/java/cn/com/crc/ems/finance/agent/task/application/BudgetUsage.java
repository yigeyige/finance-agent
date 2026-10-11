package cn.com.crc.ems.finance.agent.task.application;

import java.math.BigDecimal;

/**
 *
 * @Description: 一次模型消费的可审计预算增量。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record BudgetUsage(int steps, int tokens, BigDecimal cost) {

    public BudgetUsage {
        if (steps < 0 || tokens < 0 || cost == null || cost.signum() < 0) {
            throw new IllegalArgumentException("budget usage must be non-negative");
        }
    }
}
