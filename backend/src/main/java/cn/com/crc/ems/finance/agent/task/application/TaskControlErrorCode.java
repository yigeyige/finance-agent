package cn.com.crc.ems.finance.agent.task.application;

/**
 *
 * @Description: 暂停与取消边界的稳定错误码。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public enum TaskControlErrorCode {
    MODEL_UNAVAILABLE,
    SOURCE_UNAVAILABLE,
    BUDGET_EXHAUSTED,
    RECOVERABLE_FAILURE,
    TASK_CANCELLED
}
