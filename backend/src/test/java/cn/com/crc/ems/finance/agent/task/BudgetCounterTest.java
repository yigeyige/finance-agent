package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.application.BudgetCounter;
import cn.com.crc.ems.finance.agent.task.application.BudgetSnapshot;
import cn.com.crc.ems.finance.agent.task.application.BudgetUsage;

class BudgetCounterTest {

    @Test
    void allowsUsageWithinAllLimits() {
        BudgetSnapshot current = new BudgetSnapshot(3, 100, new BigDecimal("2.00"), 1, 20,
                new BigDecimal("0.50"));

        var decision = new BudgetCounter().consume(current,
                new BudgetUsage(1, 30, new BigDecimal("0.75")));

        assertThat(decision.allowed()).isTrue();
        assertThat(decision.snapshot().usedTokens()).isEqualTo(50);
    }

    @Test
    void rejectsExhaustedBudgetBeforeModelCall() {
        BudgetSnapshot current = new BudgetSnapshot(2, 100, new BigDecimal("2.00"), 2, 20,
                new BigDecimal("0.50"));

        var decision = new BudgetCounter().consume(current, new BudgetUsage(1, 1, BigDecimal.ZERO));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.errorCode()).isEqualTo("BUDGET_EXHAUSTED");
    }
}
