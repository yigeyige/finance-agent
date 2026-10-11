package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 请求对某个 checkpoint 执行无副作用的恢复判定。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record CheckpointRecoveryRequest(UUID checkpointId, UUID taskId, UUID userId, UUID sessionId,
        String ownerInstance, String idempotencyKey, Instant observedAt) {

    public CheckpointRecoveryRequest {
        requireId(checkpointId, "checkpointId");
        requireId(taskId, "taskId");
        requireId(userId, "userId");
        requireId(sessionId, "sessionId");
        if (observedAt == null) {
            throw new IllegalArgumentException("observedAt is required");
        }
        if (ownerInstance != null && ownerInstance.isBlank()) {
            throw new IllegalArgumentException("ownerInstance must not be blank");
        }
        if (idempotencyKey != null && idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey must not be blank");
        }
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }
}
