package cn.com.crc.ems.finance.agent.evidence.domain;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 已按字段策略裁剪的来源事实快照。 */
public record FactSnapshot(UUID snapshotId, UUID taskId, UUID stepId, UUID sourceBindingId,
        String sourceObjectType, String sourceObjectKey, String allowedFields, Instant checkedAt,
        Instant effectiveFrom, Instant effectiveTo, EffectiveClass effectiveClass, String sourceVersion,
        String resultDigest, String fieldPolicyVersion, String schemaVersion, UUID createdBy,
        Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public FactSnapshot {
        if (snapshotId == null || taskId == null || stepId == null || sourceBindingId == null
                || checkedAt == null || effectiveClass == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("snapshot identity, classification and timestamps are required");
        }
        if (blank(sourceObjectType) || blank(sourceObjectKey) || blank(allowedFields)
                || blank(resultDigest) || blank(fieldPolicyVersion) || blank(schemaVersion)) {
            throw new IllegalArgumentException("snapshot source, fields and digest are invalid");
        }
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.isAfter(effectiveTo)) {
            throw new IllegalArgumentException("snapshot effective range is invalid");
        }
        new VersionedJson(schemaVersion, allowedFields).requireObject();
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
