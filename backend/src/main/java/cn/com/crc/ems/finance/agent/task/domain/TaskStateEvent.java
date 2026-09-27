package cn.com.crc.ems.finance.agent.task.domain;

import java.time.Instant;
import java.util.UUID;

/** 任务状态改变的最小事件元数据，持久化时复用治理审计摘要链。 */
public record TaskStateEvent(UUID eventId, UUID taskId, TaskStatus fromStatus, TaskStatus toStatus,
        String reason, UUID actorUserId, Instant occurredAt, long rowVersion) {

    public TaskStateEvent {
        if (eventId == null || taskId == null || fromStatus == null || toStatus == null || occurredAt == null) {
            throw new IllegalArgumentException("state event identity, status and timestamp are required");
        }
        if (reason == null || reason.isBlank() || rowVersion < 0) {
            throw new IllegalArgumentException("state event reason or row version is invalid");
        }
    }
}
