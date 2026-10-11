package cn.com.crc.ems.finance.agent.task.domain;

/**
 *
 * @Description: 调查步骤执行状态。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public enum StepStatus {
    PROPOSED, WAITING_APPROVAL, APPROVED, RUNNING, SUCCEEDED, PARTIAL, FAILED, CANCELLED, STALE
}
