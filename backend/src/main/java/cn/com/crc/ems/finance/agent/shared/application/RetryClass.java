package cn.com.crc.ems.finance.agent.shared.application;

/** API 调用方可据此决定重试、新审批或人工介入。 */
public enum RetryClass {
    RETRYABLE,
    NEW_APPROVAL_REQUIRED,
    MANUAL_RECONCILIATION,
    NOT_RETRYABLE
}
