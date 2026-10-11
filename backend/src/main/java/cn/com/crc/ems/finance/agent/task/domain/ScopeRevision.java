package cn.com.crc.ems.finance.agent.task.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 代表不可原位修改、可重新确认的任务范围修订。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ScopeRevision(UUID scopeRevisionId, UUID taskId, int revisionNo, String systemCode,
        VersionedJson objectSelector, VersionedJson effectiveTimeRange, VersionedJson includedPaths,
        VersionedJson excludedPaths, VersionedJson allowedReadFields, VersionedJson allowedRetainFields,
        VersionedJson allowedModelFields, UUID confirmedBy, Instant confirmedAt, String scopeDigest,
        String schemaVersion, UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public ScopeRevision {
        if (scopeRevisionId == null || taskId == null || objectSelector == null || effectiveTimeRange == null
                || includedPaths == null || excludedPaths == null || allowedReadFields == null
                || allowedRetainFields == null || allowedModelFields == null || confirmedBy == null
                || confirmedAt == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("scope identity, JSON fields, confirmation and timestamps are required");
        }
        if (revisionNo < 1 || systemCode == null || systemCode.isBlank()
                || scopeDigest == null || scopeDigest.isBlank() || schemaVersion == null || schemaVersion.isBlank()) {
            throw new IllegalArgumentException("scope revision fields are invalid");
        }
        objectSelector.requireObject();
    }
}
