package cn.com.crc.ems.finance.agent.packet.application;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.evidence.application.FindingRepository;
import cn.com.crc.ems.finance.agent.evidence.application.ProfessionalConfirmationRepository;
import cn.com.crc.ems.finance.agent.evidence.domain.FindingStatus;
import cn.com.crc.ems.finance.agent.evidence.domain.JudgmentMode;
import cn.com.crc.ems.finance.agent.packet.domain.PacketDraft;
import cn.com.crc.ems.finance.agent.packet.domain.PacketReview;
import cn.com.crc.ems.finance.agent.packet.domain.ReviewDecision;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;

/** 校验草稿绑定和专业确认后追加复核记录。 */
@Service
public class PacketReviewApplicationService {

    private final PacketDraftRepository draftRepository;
    private final PacketReviewRepository reviewRepository;
    private final FindingRepository findingRepository;
    private final ProfessionalConfirmationRepository confirmationRepository;
    private final TaskAuditEventAppender auditEventAppender;
    private final Clock applicationClock;

    public PacketReviewApplicationService(PacketDraftRepository draftRepository, PacketReviewRepository reviewRepository,
            FindingRepository findingRepository, ProfessionalConfirmationRepository confirmationRepository,
            TaskAuditEventAppender auditEventAppender, Clock applicationClock) {
        this.draftRepository = draftRepository;
        this.reviewRepository = reviewRepository;
        this.findingRepository = findingRepository;
        this.confirmationRepository = confirmationRepository;
        this.auditEventAppender = auditEventAppender;
        this.applicationClock = applicationClock;
    }

    /** 追加一条绑定当前草稿的复核；批准前必须完成专业确认。 */
    @Transactional
    public PacketReviewResult review(UUID taskId, UUID draftId, UUID reviewerUserId, String contentDigest,
            UUID scopeRevisionId, UUID runtimeBundleId, ReviewDecision decision, String comment) {
        requireId(taskId, "taskId");
        requireId(draftId, "draftId");
        requireId(reviewerUserId, "reviewerUserId");
        if (decision == null || contentDigest == null || contentDigest.isBlank()
                || scopeRevisionId == null || runtimeBundleId == null) {
            throw new IllegalArgumentException("review bindings and decision are required");
        }
        PacketDraft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("packet draft not found"));
        if (!taskId.equals(draft.taskId())) {
            throw new IllegalArgumentException("draft does not belong to task");
        }
        Instant now = Instant.now(applicationClock);
        PacketReview review = new PacketReview(UuidGenerator.newUuid(), draftId, reviewerUserId, contentDigest,
                scopeRevisionId, runtimeBundleId, decision, now, comment, reviewerUserId, now, reviewerUserId, now);
        review.requireMatches(draft);
        if (decision == ReviewDecision.APPROVE) {
            requireProfessionalConfirmations(taskId);
        }
        reviewRepository.insert(review);
        auditEventAppender.append(UuidGenerator.newUuid(), taskId, reviewerUserId, "PACKET_DRAFT_REVIEWED",
                "packet_draft", draftId.toString(), review.reviewedAt(), payload -> {
                    payload.put("decision", decision.name());
                    payload.put("contentDigest", contentDigest);
                    payload.put("scopeRevisionId", scopeRevisionId.toString());
                    payload.put("runtimeBundleId", runtimeBundleId.toString());
                });
        return new PacketReviewResult(draft, review);
    }

    private void requireProfessionalConfirmations(UUID taskId) {
        boolean incomplete = findingRepository.findByTaskId(taskId).stream()
                .filter(finding -> finding.judgmentMode() == JudgmentMode.HUMAN_CONFIRMED)
                .anyMatch(finding -> finding.status() != FindingStatus.CONFIRMED
                        || confirmationRepository.findByFindingId(finding.findingId()).isEmpty());
        if (incomplete) {
            throw new IllegalStateException("professional confirmations are incomplete");
        }
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }
}
