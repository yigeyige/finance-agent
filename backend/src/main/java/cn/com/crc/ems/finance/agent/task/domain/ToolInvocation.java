package cn.com.crc.ems.finance.agent.task.domain;

import java.time.Instant;
import java.util.UUID;

/** 代表绑定审批的可恢复工具调用意图及其结果摘要。 */
public record ToolInvocation(UUID invocationId, UUID taskId, UUID stepId, UUID approvalId,
        String toolCode, String idempotencyKey, String requestDigest, String responseDigest,
        InvocationStatus status, Instant startedAt, Instant completedAt, String errorCode,
        UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public ToolInvocation {
        if (invocationId == null || taskId == null || stepId == null || approvalId == null
                || status == null || startedAt == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("invocation identity, status and timestamps are required");
        }
        if (blank(toolCode) || blank(idempotencyKey) || blank(requestDigest)) {
            throw new IllegalArgumentException("invocation keys and digest are invalid");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
