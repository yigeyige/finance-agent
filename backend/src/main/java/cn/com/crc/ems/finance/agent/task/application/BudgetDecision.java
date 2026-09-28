package cn.com.crc.ems.finance.agent.task.application;

/** 预算计数结果；拒绝结果不得继续执行模型调用。 */
public record BudgetDecision(boolean allowed, BudgetSnapshot snapshot, String errorCode) {

    public BudgetDecision {
        if (snapshot == null || !allowed && (errorCode == null || errorCode.isBlank())) {
            throw new IllegalArgumentException("budget decision is invalid");
        }
        if (allowed && errorCode != null) {
            throw new IllegalArgumentException("allowed budget decision must not contain an error");
        }
    }
}
