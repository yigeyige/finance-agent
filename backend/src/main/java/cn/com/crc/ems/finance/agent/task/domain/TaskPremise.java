package cn.com.crc.ems.finance.agent.task.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 代表运维人员提供的、独立核实的前提留痕。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record TaskPremise(UUID premiseId, UUID taskId, String subjectKey, UUID providedBy, Instant providedAt,
        boolean independentlyVerified, UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public TaskPremise {
        if (premiseId == null || taskId == null || providedBy == null || providedAt == null
                || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("premise identity and timestamps are required");
        }
        if (subjectKey == null || subjectKey.isBlank()) {
            throw new IllegalArgumentException("premise subject must not be blank");
        }
    }
}
