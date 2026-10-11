package cn.com.crc.ems.finance.agent.task.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 代表任务中的一个有序调查步骤。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record InvestigationStep(UUID stepId, UUID taskId, int sequenceNo, String goal,
        boolean recommended, StepStatus status, Instant createdAt, Instant completedAt,
        UUID createdBy, Instant updatedAt, UUID updatedBy) {

    public InvestigationStep {
        if (stepId == null || taskId == null || status == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("step identity, status and createdAt are required");
        }
        if (sequenceNo < 1 || goal == null || goal.isBlank()) {
            throw new IllegalArgumentException("sequenceNo and goal are invalid");
        }
    }
}
