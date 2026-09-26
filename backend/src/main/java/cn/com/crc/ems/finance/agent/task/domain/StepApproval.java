package cn.com.crc.ems.finance.agent.task.domain;

import java.time.Instant;
import java.util.UUID;

/** 代表绑定步骤、范围和查询策略的审批卡。 */
public record StepApproval(UUID approvalId, UUID stepId, int revisionNo, UUID approvedBy,
        Instant approvedAt, Instant expiresAt, String goalHash, UUID sourceBindingId,
        UUID scopeRevisionId, String fieldPolicyVersion, String queryTemplateVersion,
        int maxCalls, int usedCalls, String approvalDigest, ApprovalStatus status,
        UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public StepApproval {
        if (approvalId == null || stepId == null || approvedBy == null || approvedAt == null
                || sourceBindingId == null || scopeRevisionId == null || status == null
                || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("approval identity, binding and timestamps are required");
        }
        if (revisionNo < 1 || maxCalls < 1 || usedCalls < 0 || usedCalls > maxCalls
                || blank(goalHash) || blank(fieldPolicyVersion) || blank(queryTemplateVersion)
                || blank(approvalDigest)) {
            throw new IllegalArgumentException("approval limits or versions are invalid");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
