package cn.com.crc.ems.finance.agent.evidence.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 不可由确定性程序完成的判断的逐项人工确认记录。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ProfessionalConfirmation(UUID confirmationId, UUID findingId, UUID confirmedBy,
        Instant confirmedAt, UUID scopeRevisionId, ConfirmationDecision decision, String rationale,
        String evidenceRefs, UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public ProfessionalConfirmation {
        if (confirmationId == null || findingId == null || confirmedBy == null || confirmedAt == null
                || scopeRevisionId == null || decision == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("confirmation identity, decision and timestamps are required");
        }
        if (rationale == null || rationale.isBlank() || evidenceRefs == null || evidenceRefs.isBlank()) {
            throw new IllegalArgumentException("confirmation rationale and evidence references are required");
        }
    }
}
