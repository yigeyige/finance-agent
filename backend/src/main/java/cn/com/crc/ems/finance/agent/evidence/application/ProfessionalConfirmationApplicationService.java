package cn.com.crc.ems.finance.agent.evidence.application;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.evidence.domain.ConfirmationDecision;
import cn.com.crc.ems.finance.agent.evidence.domain.Finding;
import cn.com.crc.ems.finance.agent.evidence.domain.FindingStatus;
import cn.com.crc.ems.finance.agent.evidence.domain.JudgmentMode;
import cn.com.crc.ems.finance.agent.evidence.domain.ProfessionalConfirmation;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJson;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;

/** 逐条处理专业判断确认，并保留理由、证据引用和审计摘要。 */
@Service
public class ProfessionalConfirmationApplicationService {

    private final FindingRepository findingRepository;
    private final ProfessionalConfirmationRepository confirmationRepository;
    private final TaskAuditEventAppender auditEventAppender;
    private final Clock applicationClock;

    public ProfessionalConfirmationApplicationService(FindingRepository findingRepository,
            ProfessionalConfirmationRepository confirmationRepository, TaskAuditEventAppender auditEventAppender,
            Clock applicationClock) {
        this.findingRepository = findingRepository;
        this.confirmationRepository = confirmationRepository;
        this.auditEventAppender = auditEventAppender;
        this.applicationClock = applicationClock;
    }

    /** 写入一个 finding 的专业确认，不允许批量或覆盖既有确认。 */
    @Transactional
    public ProfessionalConfirmationResult confirm(UUID taskId, UUID findingId, UUID actorUserId,
            UUID scopeRevisionId, ConfirmationDecision decision, String rationale, String evidenceRefs) {
        requireId(taskId, "taskId");
        requireId(findingId, "findingId");
        requireId(actorUserId, "actorUserId");
        requireId(scopeRevisionId, "scopeRevisionId");
        if (decision == null || rationale == null || rationale.isBlank()
                || evidenceRefs == null || evidenceRefs.isBlank()) {
            throw new IllegalArgumentException("decision, rationale and evidence references are required");
        }
        Finding finding = findingRepository.findById(findingId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("finding not found"));
        if (!taskId.equals(finding.taskId())) {
            throw new IllegalArgumentException("finding does not belong to task");
        }
        if (!scopeRevisionId.equals(finding.scopeRevisionId())) {
            throw new IllegalStateException("finding scope revision is stale");
        }
        if (finding.judgmentMode() == JudgmentMode.DETERMINISTIC) {
            throw new IllegalStateException("deterministic finding cannot be professionally confirmed");
        }
        if (confirmationRepository.findByFindingId(findingId).isPresent()) {
            throw new IllegalStateException("finding already has a professional confirmation");
        }
        Instant now = Instant.now(applicationClock);
        String trimmedRationale = rationale.trim();
        String trimmedEvidenceRefs = canonicalEvidenceRefs(evidenceRefs);
        ProfessionalConfirmation confirmation = new ProfessionalConfirmation(UuidGenerator.newUuid(), findingId,
                actorUserId, now, scopeRevisionId, decision, trimmedRationale, trimmedEvidenceRefs, actorUserId, now,
                actorUserId, now);
        confirmationRepository.insert(confirmation);
        FindingStatus targetStatus = switch (decision) {
        case CONFIRM -> FindingStatus.CONFIRMED;
        case REJECT -> FindingStatus.WITHDRAWN;
        case NEEDS_MORE_EVIDENCE -> FindingStatus.DRAFT;
        };
        if (!findingRepository.updateStatus(findingId, targetStatus, actorUserId, now)) {
            throw new RepositoryEntityNotFoundException("finding status update failed");
        }
        Finding updatedFinding = new Finding(finding.findingId(), finding.taskId(), finding.findingType(),
                finding.statement(), finding.judgmentMode(), finding.effectiveClass(), finding.scopeRevisionId(),
                targetStatus, finding.createdBy(), finding.createdAt(), actorUserId, now);
        auditEventAppender.append(UuidGenerator.newUuid(), taskId, actorUserId, "PROFESSIONAL_CONFIRMATION_RECORDED",
                "finding", findingId.toString(), now, payload -> {
                    payload.put("decision", decision.name());
                    payload.put("rationaleDigest", Sha256Digest.hex(trimmedRationale));
                    payload.put("evidenceRefsDigest", Sha256Digest.hex(trimmedEvidenceRefs));
                    payload.put("scopeRevisionId", scopeRevisionId.toString());
                    payload.put("findingStatus", targetStatus.name());
                });
        return new ProfessionalConfirmationResult(updatedFinding, confirmation);
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static String canonicalEvidenceRefs(String value) {
        var node = CanonicalJsonCodec.parse(value.trim());
        if (!node.isArray() || node.isEmpty()) {
            throw new IllegalArgumentException("evidence references must be a non-empty JSON array");
        }
        return CanonicalJson.of(node.toString()).value();
    }
}
