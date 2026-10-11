package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 *
 * @Description: 请求推进任务状态的应用命令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record TaskStateTransitionCommand(UUID taskId, TaskStatus targetStatus, String reason,
        UUID actorUserId, Instant occurredAt, boolean approvalStillValid, UUID scopeRevisionId,
        UUID stepId) {

    public TaskStateTransitionCommand(UUID taskId, TaskStatus targetStatus, String reason,
            UUID actorUserId, Instant occurredAt, boolean approvalStillValid) {
        this(taskId, targetStatus, reason, actorUserId, occurredAt, approvalStillValid, null);
    }

    public TaskStateTransitionCommand(UUID taskId, TaskStatus targetStatus, String reason,
            UUID actorUserId, Instant occurredAt, boolean approvalStillValid, UUID scopeRevisionId) {
        this(taskId, targetStatus, reason, actorUserId, occurredAt, approvalStillValid, scopeRevisionId, null);
    }

    public TaskStateTransitionCommand {
        if (taskId == null || targetStatus == null || occurredAt == null) {
            throw new IllegalArgumentException("taskId, targetStatus and occurredAt are required");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
    }
}
