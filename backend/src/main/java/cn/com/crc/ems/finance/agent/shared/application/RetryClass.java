package cn.com.crc.ems.finance.agent.shared.application;

/**
 *
 * @Description: API 调用方可据此决定重试、新审批或人工介入。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public enum RetryClass {
    RETRYABLE,
    NEW_APPROVAL_REQUIRED,
    MANUAL_RECONCILIATION,
    NOT_RETRYABLE
}
