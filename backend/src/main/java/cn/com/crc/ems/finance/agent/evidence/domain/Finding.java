package cn.com.crc.ems.finance.agent.evidence.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 可追溯的调查判断结果。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record Finding(UUID findingId, UUID taskId, String findingType, String statement,
        JudgmentMode judgmentMode, EffectiveClass effectiveClass, UUID scopeRevisionId,
        FindingStatus status, UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public Finding {
        if (findingId == null || taskId == null || scopeRevisionId == null || judgmentMode == null
                || effectiveClass == null || status == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("finding identity, classification and timestamps are required");
        }
        if (blank(findingType) || blank(statement)) {
            throw new IllegalArgumentException("finding type and statement must not be blank");
        }
    }

    /** 校验该判断是否具备进入可发布状态的证据链。 */
    public void requirePublishable(boolean hasEvidence, boolean hasWitness, boolean hasProfessionalConfirmation) {
        if (status != FindingStatus.CONFIRMED || !hasEvidence) {
            throw new IllegalStateException("finding must be confirmed and supported by evidence");
        }
        if (judgmentMode == JudgmentMode.DETERMINISTIC && !hasWitness) {
            throw new IllegalStateException("deterministic finding requires computation witness");
        }
        if (judgmentMode == JudgmentMode.HUMAN_CONFIRMED && !hasProfessionalConfirmation) {
            throw new IllegalStateException("professional finding requires confirmation");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
