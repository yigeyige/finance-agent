package cn.com.crc.ems.finance.agent.evidence.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.evidence.application.ProfessionalConfirmationResult;

/**
 *
 * @Description: 专业确认结果，返回状态和确认元数据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ProfessionalConfirmationResponse(UUID taskId, UUID findingId, String findingStatus,
        UUID confirmationId, UUID confirmedBy, Instant confirmedAt, UUID scopeRevisionId, String decision,
        String rationale, String evidenceRefs) {

    public static ProfessionalConfirmationResponse from(ProfessionalConfirmationResult result) {
        var confirmation = result.confirmation();
        return new ProfessionalConfirmationResponse(result.finding().taskId(), result.finding().findingId(),
                result.finding().status().name(), confirmation.confirmationId(), confirmation.confirmedBy(),
                confirmation.confirmedAt(), confirmation.scopeRevisionId(), confirmation.decision().name(),
                confirmation.rationale(), confirmation.evidenceRefs());
    }
}
