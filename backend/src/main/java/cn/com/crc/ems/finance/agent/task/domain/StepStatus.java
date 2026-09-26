package cn.com.crc.ems.finance.agent.task.domain;

/** 调查步骤执行状态。 */
public enum StepStatus {
    PROPOSED, WAITING_APPROVAL, APPROVED, RUNNING, SUCCEEDED, PARTIAL, FAILED, CANCELLED, STALE
}
