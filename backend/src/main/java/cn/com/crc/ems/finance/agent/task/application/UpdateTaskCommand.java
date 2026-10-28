package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 更新任务基础信息的应用命令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-27 20:00:00
 */
public record UpdateTaskCommand(UUID taskId, String originalQuestion, UUID businessSceneSkillId,
        UUID databaseConfigId, UUID actorUserId, Instant occurredAt) {

    public UpdateTaskCommand {
        requireId(taskId, "taskId");
        requireText(originalQuestion, "originalQuestion");
        requireId(actorUserId, "actorUserId");
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt is required");
        }
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
    }
}
