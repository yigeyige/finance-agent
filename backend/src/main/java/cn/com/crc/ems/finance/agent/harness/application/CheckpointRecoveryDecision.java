package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 恢复判定的有限动作集合，避免调用方自行推断是否可以执行。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public enum CheckpointRecoveryDecision {
    RESUME,
    REPLAY_RESULT,
    NEW_APPROVAL_REQUIRED,
    MANUAL_RECONCILIATION
}
