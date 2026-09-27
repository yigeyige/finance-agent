package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/** 请求推进任务状态的应用命令。 */
public record TaskStateTransitionCommand(UUID taskId, TaskStatus targetStatus, String reason,
        UUID actorUserId, Instant occurredAt, boolean approvalStillValid) {

    public TaskStateTransitionCommand {
        if (taskId == null || targetStatus == null || occurredAt == null) {
            throw new IllegalArgumentException("taskId, targetStatus and occurredAt are required");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
    }
}
