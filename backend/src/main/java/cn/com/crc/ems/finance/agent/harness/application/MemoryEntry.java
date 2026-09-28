package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/**
 *
 * @Description: 仅保存非敏感经验摘要的长期记忆条目，不保存证据或业务结论。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 11:00:00
 */
public record MemoryEntry(UUID memoryId, String sourceTaskCategory, String applicabilityScope,
        String summary, String contentDigest, MemoryStatus status, UUID approvedBy,
        Instant createdAt, Instant updatedAt) {

    public MemoryEntry {
        if (memoryId == null || sourceTaskCategory == null || sourceTaskCategory.isBlank()
                || applicabilityScope == null || applicabilityScope.isBlank() || summary == null || summary.isBlank()
                || contentDigest == null || !contentDigest.matches("[0-9a-f]{64}")
                || !Sha256Digest.hex(summary).equals(contentDigest) || status == null || createdAt == null
                || updatedAt == null) {
            throw new IllegalArgumentException("memory entry is invalid");
        }
        if (status == MemoryStatus.ACTIVE && approvedBy == null) {
            throw new IllegalArgumentException("active memory requires approval");
        }
    }

    public MemoryEntry activate(UUID approver, Instant at) {
        if (status != MemoryStatus.PENDING_APPROVAL || approver == null || at == null) {
            throw new IllegalStateException("memory entry is not awaiting approval");
        }
        return new MemoryEntry(memoryId, sourceTaskCategory, applicabilityScope, summary, contentDigest,
                MemoryStatus.ACTIVE, approver, createdAt, at);
    }

    public MemoryEntry withdraw(UUID actor, Instant at) {
        if (status != MemoryStatus.ACTIVE || actor == null || at == null) {
            throw new IllegalStateException("only active memory can be withdrawn");
        }
        return new MemoryEntry(memoryId, sourceTaskCategory, applicabilityScope, summary, contentDigest,
                MemoryStatus.WITHDRAWN, approvedBy, createdAt, at);
    }
}
