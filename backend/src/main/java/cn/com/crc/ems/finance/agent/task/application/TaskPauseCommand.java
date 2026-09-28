package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/** 请求将任务置为明确暂停状态。 */
public record TaskPauseCommand(UUID taskId, TaskControlErrorCode errorCode, String reason,
        UUID actorUserId, Instant occurredAt) {

    public TaskPauseCommand {
        if (taskId == null || errorCode == null || occurredAt == null || reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("task pause command is invalid");
        }
        if (errorCode == TaskControlErrorCode.TASK_CANCELLED) {
            throw new IllegalArgumentException("cancel error code cannot pause a task");
        }
    }
}
