package cn.com.crc.ems.finance.agent.harness.application;

/** 恢复判定的有限动作集合，避免调用方自行推断是否可以执行。 */
public enum CheckpointRecoveryDecision {
    RESUME,
    REPLAY_RESULT,
    NEW_APPROVAL_REQUIRED,
    MANUAL_RECONCILIATION
}
