package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 创建调查任务草稿的应用命令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record CreateTaskDraftCommand(String title, String originalQuestion, UUID initiatorUserId,
        UUID runtimeBundleId, UUID actorUserId, Instant occurredAt, UUID businessSceneSkillId, UUID databaseConfigId) {

    public CreateTaskDraftCommand(String title, String originalQuestion, UUID initiatorUserId,
            UUID runtimeBundleId, UUID actorUserId, Instant occurredAt) {
        this(title, originalQuestion, initiatorUserId, runtimeBundleId, actorUserId, occurredAt, null, null);
    }

    public CreateTaskDraftCommand {
        requireText(title, "title");
        requireText(originalQuestion, "originalQuestion");
        requireId(initiatorUserId, "initiatorUserId");
        requireId(runtimeBundleId, "runtimeBundleId");
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
