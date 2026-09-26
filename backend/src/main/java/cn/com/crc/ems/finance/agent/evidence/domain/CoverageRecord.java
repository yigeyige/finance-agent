package cn.com.crc.ems.finance.agent.evidence.domain;

import java.time.Instant;
import java.util.UUID;

/** 描述批准范围遍历结果及其覆盖度的证据记录。 */
public record CoverageRecord(UUID coverageId, UUID taskId, UUID stepId, UUID scopeRevisionId,
        String queryTemplateVersion, String queryConditionDigest, String enumerationMethod,
        Integer pageCount, Long expectedTotal, Long observedTotal, String traversedPaths,
        String uncoveredPaths, String failures, String sourceWatermark, String resultSetDigest,
        CoverageCompleteness completeness, Instant checkedAt, UUID createdBy, Instant createdAt,
        UUID updatedBy, Instant updatedAt) {

    public CoverageRecord {
        if (coverageId == null || taskId == null || stepId == null || scopeRevisionId == null
                || completeness == null || checkedAt == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("coverage identity, completeness and timestamps are required");
        }
        if (blank(queryTemplateVersion) || blank(queryConditionDigest) || blank(enumerationMethod)
                || blank(traversedPaths) || blank(uncoveredPaths) || blank(failures) || blank(resultSetDigest)) {
            throw new IllegalArgumentException("coverage query, path and digest fields are invalid");
        }
        if (pageCount != null && pageCount < 0 || expectedTotal != null && expectedTotal < 0
                || observedTotal != null && observedTotal < 0) {
            throw new IllegalArgumentException("coverage counts cannot be negative");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
