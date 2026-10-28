package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 追加运维提供任务前提的应用命令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record AddTaskPremiseCommand(UUID taskId, String subjectKey, UUID providedBy, Instant occurredAt) {

    public AddTaskPremiseCommand {
        requireId(taskId, "taskId");
        requireText(subjectKey, "subjectKey");
        requireId(providedBy, "providedBy");
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
