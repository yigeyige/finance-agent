package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 长期记忆审批和撤回的审计记录。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 11:00:00
 */
public record MemoryApprovalEvent(UUID memoryId, MemoryStatus fromStatus, MemoryStatus toStatus,
        UUID actorId, Instant occurredAt, String contentDigest) {

    public MemoryApprovalEvent {
        if (memoryId == null || fromStatus == null || toStatus == null || actorId == null || occurredAt == null
                || contentDigest == null || !contentDigest.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("memory approval event is invalid");
        }
    }
}
