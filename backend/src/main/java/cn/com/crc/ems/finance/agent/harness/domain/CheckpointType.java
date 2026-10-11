package cn.com.crc.ems.finance.agent.harness.domain;

/**
 *
 * @Description: 业务运行时允许落盘的六类安全 checkpoint。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public enum CheckpointType {
    APPROVAL_GRANTED,
    INVOCATION_INTENT_RECORDED,
    RESULT_TRIMMED,
    DETERMINISTIC_COMPUTATION_COMPLETED,
    DRAFT_CREATED,
    PUBLICATION_REVIEWED
}
