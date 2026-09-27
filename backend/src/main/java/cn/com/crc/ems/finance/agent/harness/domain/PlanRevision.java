package cn.com.crc.ems.finance.agent.harness.domain;

import java.time.Instant;
import java.util.UUID;

/** Harness 计划的追加修订，计划本身不是业务审批权威。 */
public record PlanRevision(UUID planRevisionId, UUID sessionId, UUID taskId, UUID userId,
        int revisionNo, StateReference planRef, UUID createdBy, Instant createdAt, UUID updatedBy,
        Instant updatedAt) {

    public PlanRevision {
        requireId(planRevisionId, "planRevisionId");
        requireId(sessionId, "sessionId");
        requireId(taskId, "taskId");
        requireId(userId, "userId");
        if (revisionNo < 1) {
            throw new IllegalArgumentException("revisionNo must be positive");
        }
        if (planRef == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("planRef and audit timestamps are required");
        }
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }
}
