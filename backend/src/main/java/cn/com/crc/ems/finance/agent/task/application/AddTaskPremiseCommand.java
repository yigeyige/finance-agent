package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/** 追加运维提供任务前提的应用命令。 */
public record AddTaskPremiseCommand(UUID taskId, String premiseType, String subjectKey, String statement,
        UUID providedBy, String sourceDescription, Instant occurredAt) {

    public AddTaskPremiseCommand {
        requireId(taskId, "taskId");
        requireText(premiseType, "premiseType");
        requireText(subjectKey, "subjectKey");
        requireText(statement, "statement");
        requireId(providedBy, "providedBy");
        requireText(sourceDescription, "sourceDescription");
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
