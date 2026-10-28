package cn.com.crc.ems.finance.agent.task.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 代表一项具备运行版本和乐观版本的调查任务。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record InvestigationTask(UUID taskId, String title, String originalQuestion, UUID initiatorUserId,
        TaskStatus status, UUID runtimeBundleId, UUID databaseConfigId, UUID currentScopeRevisionId, UUID currentStepId,
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
