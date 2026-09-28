package cn.com.crc.ems.finance.agent.harness.domain;

/** 业务运行时允许落盘的六类安全 checkpoint。 */
public enum CheckpointType {
    APPROVAL_GRANTED,
    INVOCATION_INTENT_RECORDED,
    RESULT_TRIMMED,
    DETERMINISTIC_COMPUTATION_COMPLETED,
    DRAFT_CREATED,
    PUBLICATION_REVIEWED
}
