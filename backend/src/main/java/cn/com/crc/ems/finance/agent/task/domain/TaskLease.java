package cn.com.crc.ems.finance.agent.task.domain;

import java.time.Instant;
import java.util.UUID;

/** 代表任务当前执行实例持有的租约。 */
public record TaskLease(UUID leaseId, UUID taskId, UUID stepId, String ownerInstance,
        Instant acquiredAt, Instant heartbeatAt, Instant expiresAt, Instant releasedAt,
        UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public TaskLease {
        if (leaseId == null || taskId == null || stepId == null || acquiredAt == null
                || heartbeatAt == null || expiresAt == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("lease identity and timestamps are required");
        }
        if (ownerInstance == null || ownerInstance.isBlank()) {
            throw new IllegalArgumentException("ownerInstance must not be blank");
        }
    }
}
