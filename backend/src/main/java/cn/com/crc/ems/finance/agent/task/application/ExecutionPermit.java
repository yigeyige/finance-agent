package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 仅在服务端进程内传递的短期步骤能力许可，不是 HTTP 或模型令牌。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:35:00
 */
public record ExecutionPermit(UUID taskId, UUID stepId, UUID approvalId, UUID scopeRevisionId,
        UUID sourceBindingId, String approvalDigest, String idempotencyKey, Instant issuedAt,
        Instant expiresAt) {

    public ExecutionPermit {
        if (taskId == null || stepId == null || approvalId == null || scopeRevisionId == null
                || sourceBindingId == null || issuedAt == null || expiresAt == null
                || !expiresAt.isAfter(issuedAt)) {
            throw new IllegalArgumentException("permit identity and lifetime are invalid");
        }
        if (approvalDigest == null || !approvalDigest.matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException("approvalDigest must be a 64-character hexadecimal digest");
        }
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey is required");
        }
    }
}
