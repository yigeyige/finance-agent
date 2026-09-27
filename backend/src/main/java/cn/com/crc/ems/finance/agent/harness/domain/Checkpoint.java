package cn.com.crc.ems.finance.agent.harness.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** 保存最小恢复引用并绑定当前任务批准上下文的 Harness checkpoint。 */
public record Checkpoint(UUID checkpointId, UUID sessionId, UUID taskId, UUID userId, UUID stepId,
        UUID scopeRevisionId, UUID approvalId, String checkpointType, StateReference stateRef,
        UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public Checkpoint {
        requireId(checkpointId, "checkpointId");
        requireId(sessionId, "sessionId");
        requireId(taskId, "taskId");
        requireId(userId, "userId");
        requireText(checkpointType, "checkpointType");
        if (stateRef == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("stateRef and audit timestamps are required");
        }
    }

    /** 校验 checkpoint 是否仍绑定当前任务、用户、session、步骤、范围和审批。 */
    public void requireCompatible(UUID currentTaskId, UUID currentUserId, UUID currentSessionId,
            UUID currentStepId, UUID currentScopeRevisionId, UUID currentApprovalId) {
        if (!Objects.equals(taskId, currentTaskId) || !Objects.equals(userId, currentUserId)
                || !Objects.equals(sessionId, currentSessionId) || !Objects.equals(stepId, currentStepId)
                || !Objects.equals(scopeRevisionId, currentScopeRevisionId)
                || !Objects.equals(approvalId, currentApprovalId)) {
            throw new IllegalArgumentException("checkpoint is stale for the current task context");
        }
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
