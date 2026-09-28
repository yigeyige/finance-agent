package cn.com.crc.ems.finance.agent.harness.application;

import java.util.UUID;

/**
 *
 * @Description: 仅供规划提示使用的记忆结果，明确不属于证据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 11:00:00
 */
public record PlanningMemoryHint(UUID memoryId, String sourceTaskCategory, String applicabilityScope,
        String summary, String contentDigest, boolean evidence) {

    public PlanningMemoryHint {
        if (memoryId == null || sourceTaskCategory == null || sourceTaskCategory.isBlank()
                || applicabilityScope == null || applicabilityScope.isBlank() || summary == null || summary.isBlank()
                || contentDigest == null || !contentDigest.matches("[0-9a-f]{64}") || evidence) {
            throw new IllegalArgumentException("planning memory hint is invalid");
        }
    }

    public static PlanningMemoryHint from(MemoryEntry entry) {
        return new PlanningMemoryHint(entry.memoryId(), entry.sourceTaskCategory(), entry.applicabilityScope(),
                entry.summary(), entry.contentDigest(), false);
    }
}
