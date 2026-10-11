package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 请求取消任务并释放当前活动租约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record TaskCancelCommand(UUID taskId, String reason, UUID actorUserId, Instant occurredAt) {

    public TaskCancelCommand {
        if (taskId == null || occurredAt == null || reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("task cancel command is invalid");
        }
    }
}
