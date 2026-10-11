package cn.com.crc.ems.finance.agent.evidence.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 判断与事实快照之间的证据引用关系。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record FindingEvidence(UUID findingId, UUID snapshotId, String relationType,
        UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public FindingEvidence {
        if (findingId == null || snapshotId == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("finding evidence identity and timestamps are required");
        }
        if (relationType == null || relationType.isBlank()) {
            throw new IllegalArgumentException("finding evidence relation type must not be blank");
        }
    }
}
