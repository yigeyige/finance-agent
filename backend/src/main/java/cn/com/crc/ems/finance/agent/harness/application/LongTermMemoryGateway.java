package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/**
 *
 * @Description: 受控长期记忆网关，提供审批、检索和撤回，检索结果仅用于规划提示。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 11:00:00
 */
public final class LongTermMemoryGateway {

    private final Clock clock;
    private final Map<UUID, MemoryEntry> entries = new ConcurrentHashMap<>();
    private final List<MemoryApprovalEvent> events = new ArrayList<>();

    public LongTermMemoryGateway() {
        this(Clock.systemUTC());
    }

    public LongTermMemoryGateway(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock is required");
    }

    /** 提交非敏感经验摘要，初始状态为待人工审批。 */
    public MemoryEntry propose(String sourceTaskCategory, String applicabilityScope, String summary,
            UUID proposer, Instant at) {
        if (sourceTaskCategory == null || sourceTaskCategory.isBlank() || applicabilityScope == null
                || applicabilityScope.isBlank() || proposer == null || at == null) {
            throw new IllegalArgumentException("memory source, scope, proposer and time are required");
        }
        MemoryPolicy.requireSafeSummary(summary);
        MemoryEntry entry = new MemoryEntry(UUID.randomUUID(), sourceTaskCategory, applicabilityScope, summary,
                Sha256Digest.hex(summary), MemoryStatus.PENDING_APPROVAL, null, at, at);
        if (entries.putIfAbsent(entry.memoryId(), entry) != null) {
            throw new IllegalStateException("memory id collision");
        }
        return entry;
    }

    /** 显式批准待审摘要并记录审批事件。 */
    public MemoryEntry approve(UUID memoryId, UUID approver) {
        MemoryEntry current = require(memoryId);
        MemoryEntry updated = current.activate(approver, Instant.now(clock));
        entries.put(memoryId, updated);
        appendEvent(current, updated, approver);
        return updated;
    }

    /** 撤回已激活摘要；撤回后检索永不返回。 */
    public MemoryEntry withdraw(UUID memoryId, UUID actor) {
        MemoryEntry current = require(memoryId);
        MemoryEntry updated = current.withdraw(actor, Instant.now(clock));
        entries.put(memoryId, updated);
        appendEvent(current, updated, actor);
        return updated;
    }

    /** 仅按完全匹配的任务类别和适用范围返回规划提示。 */
    public List<PlanningMemoryHint> searchPlanningHints(String sourceTaskCategory, String applicabilityScope) {
        if (sourceTaskCategory == null || sourceTaskCategory.isBlank() || applicabilityScope == null
                || applicabilityScope.isBlank()) {
            throw new IllegalArgumentException("memory source and scope are required");
        }
        return entries.values().stream().filter(entry -> entry.status() == MemoryStatus.ACTIVE)
                .filter(entry -> entry.sourceTaskCategory().equals(sourceTaskCategory)
                        && entry.applicabilityScope().equals(applicabilityScope))
                .map(PlanningMemoryHint::from).toList();
    }

    public List<MemoryApprovalEvent> approvalEvents() {
        synchronized (events) {
            return List.copyOf(events);
        }
    }

    private MemoryEntry require(UUID memoryId) {
        if (memoryId == null) {
            throw new IllegalArgumentException("memory id is required");
        }
        MemoryEntry entry = entries.get(memoryId);
        if (entry == null) {
            throw new IllegalArgumentException("memory entry is unknown");
        }
        return entry;
    }

    private void appendEvent(MemoryEntry before, MemoryEntry after, UUID actor) {
        synchronized (events) {
            events.add(new MemoryApprovalEvent(after.memoryId(), before.status(), after.status(), actor,
                    after.updatedAt(), after.contentDigest()));
        }
    }
}
