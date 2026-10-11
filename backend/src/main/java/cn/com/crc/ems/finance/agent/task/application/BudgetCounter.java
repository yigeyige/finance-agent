package cn.com.crc.ems.finance.agent.task.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

/**
 *
 * @Description: 无副作用计算一次预算消费是否仍在任务上限内。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Component
public class BudgetCounter {

    /** 计算增量消费；超限时返回 BUDGET_EXHAUSTED，不抛出并不隐式放行。 */
    public BudgetDecision consume(BudgetSnapshot current, BudgetUsage usage) {
        if (current == null || usage == null) {
            throw new IllegalArgumentException("current budget and usage are required");
        }
        int steps = Math.addExact(current.usedSteps(), usage.steps());
        int tokens = Math.addExact(current.usedTokens(), usage.tokens());
        BigDecimal cost = current.usedCost().add(usage.cost());
        BudgetSnapshot next = new BudgetSnapshot(current.maxSteps(), current.maxTokens(), current.maxCost(), steps,
                tokens, cost);
        if (steps > current.maxSteps() || tokens > current.maxTokens() || cost.compareTo(current.maxCost()) > 0) {
            return new BudgetDecision(false, next, "BUDGET_EXHAUSTED");
        }
        return new BudgetDecision(true, next, null);
    }
}
