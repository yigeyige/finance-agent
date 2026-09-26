package cn.com.crc.ems.finance.agent.task.domain;

import java.time.Instant;
import java.util.UUID;

/** 代表一项具备运行版本和乐观版本的调查任务。 */
public record InvestigationTask(UUID taskId, String title, String originalQuestion, UUID initiatorUserId,
        TaskStatus status, UUID runtimeBundleId, UUID currentScopeRevisionId, UUID currentStepId,
        Instant createdAt, Instant updatedAt, long rowVersion, Instant closedAt, UUID createdBy, UUID updatedBy) {

    public InvestigationTask {
        if (taskId == null || initiatorUserId == null || runtimeBundleId == null
                || createdAt == null || updatedAt == null || status == null) {
            throw new IllegalArgumentException("task identity, status and timestamps are required");
        }
        if (title == null || title.isBlank() || originalQuestion == null || originalQuestion.isBlank()) {
            throw new IllegalArgumentException("title and originalQuestion must not be blank");
        }
        if (rowVersion < 0) {
            throw new IllegalArgumentException("rowVersion must not be negative");
        }
    }
}
