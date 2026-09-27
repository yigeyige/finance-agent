package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 创建或复用步骤审批 revision 的应用命令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:30:00
 */
public record CreateStepApprovalCommand(UUID taskId, UUID stepId, String goalHash, UUID sourceBindingId,
        UUID scopeRevisionId, String fieldPolicyVersion, String queryTemplateVersion, int maxCalls,
        Instant expiresAt, long expectedRowVersion, ApprovalDecision decision, UUID actorUserId,
        Instant occurredAt) {

    public CreateStepApprovalCommand {
        if (taskId == null || stepId == null || sourceBindingId == null || scopeRevisionId == null
                || actorUserId == null || occurredAt == null || decision == null) {
            throw new IllegalArgumentException("approval identity, actor and decision are required");
        }
        if (goalHash == null || !goalHash.matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException("goalHash must be a 64-character hexadecimal digest");
        }
        if (fieldPolicyVersion == null || fieldPolicyVersion.isBlank()
                || queryTemplateVersion == null || queryTemplateVersion.isBlank() || maxCalls < 1) {
            throw new IllegalArgumentException("approval bindings and maxCalls are invalid");
        }
        if (expiresAt != null && !expiresAt.isAfter(occurredAt)) {
            throw new IllegalArgumentException("expiresAt must be after occurredAt");
        }
        if (expectedRowVersion < 0) {
            throw new IllegalArgumentException("expectedRowVersion must not be negative");
        }
    }
}
