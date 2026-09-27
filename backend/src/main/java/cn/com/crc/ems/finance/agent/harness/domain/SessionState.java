package cn.com.crc.ems.finance.agent.harness.domain;

import java.time.Instant;
import java.util.UUID;

/** 绑定业务任务和用户的 Harness session 状态。 */
public record SessionState(UUID sessionId, UUID taskId, UUID userId, String harnessVersion,
        StateReference stateRef, UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public SessionState {
        requireId(sessionId, "sessionId");
        requireId(taskId, "taskId");
        requireId(userId, "userId");
        requireText(harnessVersion, "harnessVersion");
        if (stateRef == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("stateRef and audit timestamps are required");
        }
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
