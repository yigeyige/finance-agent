package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/** 请求取消任务并释放当前活动租约。 */
public record TaskCancelCommand(UUID taskId, String reason, UUID actorUserId, Instant occurredAt) {

    public TaskCancelCommand {
        if (taskId == null || occurredAt == null || reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("task cancel command is invalid");
        }
    }
}
