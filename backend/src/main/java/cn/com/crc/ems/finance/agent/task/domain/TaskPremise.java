package cn.com.crc.ems.finance.agent.task.domain;

import java.time.Instant;
import java.util.UUID;

/** 代表任务发起人提供、可独立核验的前提。 */
public record TaskPremise(UUID premiseId, UUID taskId, String premiseType, String subjectKey,
        String statement, UUID providedBy, String sourceDescription, Instant providedAt,
        boolean independentlyVerified, UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public TaskPremise {
        if (premiseId == null || taskId == null || providedBy == null || providedAt == null
                || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("premise identity and timestamps are required");
        }
        if (blank(premiseType) || blank(subjectKey) || blank(statement) || blank(sourceDescription)) {
            throw new IllegalArgumentException("premise text fields must not be blank");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
