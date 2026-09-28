package cn.com.crc.ems.finance.agent.harness.application;

import cn.com.crc.ems.finance.agent.harness.domain.Checkpoint;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;

/** 恢复判定及其可审计的 checkpoint/调用引用。 */
public record CheckpointRecoveryResult(CheckpointRecoveryDecision decision, Checkpoint checkpoint,
        ToolInvocation invocation, String reason) {

    public CheckpointRecoveryResult {
        if (decision == null || checkpoint == null || reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("decision, checkpoint and reason are required");
        }
    }
}
