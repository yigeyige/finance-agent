package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.harness.domain.CheckpointType;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;

/**
 *
 * @Description: 在业务安全点追加 checkpoint 的应用命令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record CheckpointRecordCommand(UUID sessionId, UUID taskId, UUID userId, UUID stepId,
        UUID scopeRevisionId, UUID approvalId, UUID runtimeBundleId, CheckpointType checkpointType,
        StateReference stateRef, UUID actorId, Instant occurredAt) {

    public CheckpointRecordCommand {
        requireId(sessionId, "sessionId");
        requireId(taskId, "taskId");
        requireId(userId, "userId");
        requireId(stepId, "stepId");
        requireId(scopeRevisionId, "scopeRevisionId");
        requireId(approvalId, "approvalId");
        requireId(runtimeBundleId, "runtimeBundleId");
        if (checkpointType == null || stateRef == null || occurredAt == null) {
            throw new IllegalArgumentException("checkpoint type, stateRef and occurredAt are required");
        }
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }
}
