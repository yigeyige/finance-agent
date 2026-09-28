package cn.com.crc.ems.finance.agent.packet.domain;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.application.RecheckDecision;

/** 可追溯的发布前范围重查记录。 */
public record ScopeRecheckRecord(UUID recheckId, UUID taskId, UUID scopeRevisionId, Instant checkedAt,
        RecheckDecision decision, String resultDigest, String diffSummary, UUID createdBy,
        Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public ScopeRecheckRecord {
        if (recheckId == null || taskId == null || scopeRevisionId == null || checkedAt == null
                || decision == null || createdAt == null || updatedAt == null
                || blank(resultDigest) || blank(diffSummary)) {
            throw new IllegalArgumentException("scope recheck record is invalid");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
