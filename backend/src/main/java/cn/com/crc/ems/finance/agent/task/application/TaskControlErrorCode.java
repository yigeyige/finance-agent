package cn.com.crc.ems.finance.agent.task.application;

/** 暂停与取消边界的稳定错误码。 */
public enum TaskControlErrorCode {
    MODEL_UNAVAILABLE,
    SOURCE_UNAVAILABLE,
    BUDGET_EXHAUSTED,
    RECOVERABLE_FAILURE,
    TASK_CANCELLED
}
