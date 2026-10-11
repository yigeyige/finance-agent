package cn.com.crc.ems.finance.agent.harness.application;

import cn.com.crc.ems.finance.agent.harness.domain.Checkpoint;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;

/**
 *
 * @Description: 恢复判定及其可审计的 checkpoint/调用引用。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record CheckpointRecoveryResult(CheckpointRecoveryDecision decision, Checkpoint checkpoint,
        ToolInvocation invocation, String reason) {

    public CheckpointRecoveryResult {
        if (decision == null || checkpoint == null || reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("decision, checkpoint and reason are required");
        }
    }
}
