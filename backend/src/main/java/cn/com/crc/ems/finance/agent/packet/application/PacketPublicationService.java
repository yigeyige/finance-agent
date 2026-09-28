package cn.com.crc.ems.finance.agent.packet.application;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.governance.application.AuditEventRepository;
import cn.com.crc.ems.finance.agent.governance.application.AuditOutboxTransaction;
import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.governance.domain.OutboxEvent;
import cn.com.crc.ems.finance.agent.packet.domain.PacketDraftStatus;
import cn.com.crc.ems.finance.agent.packet.domain.PacketRevision;
import cn.com.crc.ems.finance.agent.packet.domain.ReviewDecision;
import cn.com.crc.ems.finance.agent.packet.infrastructure.TransactionalPacketPublisher;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJson;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryStateViolationException;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 编排发布前全量重查，并在同一事务中写 revision、审计、outbox 和任务状态。 */
@Service
public class PacketPublicationService {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private final InvestigationTaskRepository taskRepository;
    private final PacketDraftRepository draftRepository;
    private final PacketReviewRepository reviewRepository;
    private final PacketRevisionRepository revisionRepository;
    private final ScopeRecheckService scopeRecheckService;
    private final TransactionalPacketPublisher transactionalPublisher;
    private final AuditEventRepository auditEventRepository;
    private final Clock applicationClock;

    public PacketPublicationService(InvestigationTaskRepository taskRepository, PacketDraftRepository draftRepository,
            PacketReviewRepository reviewRepository, PacketRevisionRepository revisionRepository,
            ScopeRecheckService scopeRecheckService, TransactionalPacketPublisher transactionalPublisher,
            AuditEventRepository auditEventRepository,
            Clock applicationClock) {
        this.taskRepository = taskRepository;
        this.draftRepository = draftRepository;
        this.reviewRepository = reviewRepository;
        this.revisionRepository = revisionRepository;
        this.scopeRecheckService = scopeRecheckService;
        this.transactionalPublisher = transactionalPublisher;
        this.auditEventRepository = auditEventRepository;
        this.applicationClock = applicationClock;
    }

    /** 幂等执行重查和发布；发现变化时标记草稿 stale 并阻断发布。 */
    @Transactional
    public PacketPublicationResult publish(PacketPublicationCommand command) {
        requireCommand(command);
        var existing = revisionRepository.findById(command.publicationId());
        if (existing.isPresent()) {
            return new PacketPublicationResult(existing.get(), null, true, TaskStatus.PUBLISHED);
        }
        InvestigationTask task = taskRepository.findByIdForUpdate(command.taskId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        var draft = draftRepository.findById(command.draftId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("packet draft not found"));
        if (!task.taskId().equals(draft.taskId()) || draft.status() != PacketDraftStatus.CURRENT
                || !command.scopeRevisionId().equals(draft.scopeRevisionId())
                || !command.runtimeBundleId().equals(draft.runtimeBundleId())
                || !command.contentDigest().equalsIgnoreCase(draft.contentDigest())) {
            throw new RepositoryStateViolationException("packet draft bindings are stale");
        }
        var review = reviewRepository.findByDraftId(command.draftId())
                .orElseThrow(() -> new RepositoryStateViolationException("approved review is required"));
        if (review.decision() != ReviewDecision.APPROVE || !review.contentDigest().equalsIgnoreCase(command.contentDigest())
                || !review.scopeRevisionId().equals(command.scopeRevisionId())
                || !review.runtimeBundleId().equals(command.runtimeBundleId())) {
            throw new RepositoryStateViolationException("approved review does not match draft bindings");
        }
        CanonicalJson canonical = CanonicalJson.of(command.canonicalJson());
        if (!canonical.sha256().equalsIgnoreCase(command.contentDigest()) || command.htmlSnapshot() == null
                || !command.htmlSnapshot().contains(command.contentDigest())) {
            throw new RepositoryStateViolationException("canonical content and HTML snapshot digest do not match");
        }
        Instant now = Instant.now(applicationClock);
        ScopeRecheckResult recheck = scopeRecheckService.recheck(command.taskId(), command.scopeRevisionId(),
                command.baseline(), command.current(), now, command.actorUserId());
        if (recheck.decision() != RecheckDecision.READY_TO_PUBLISH) {
            draftRepository.markStaleByScopeRevision(command.taskId(), command.scopeRevisionId(), command.actorUserId(), now);
            InvestigationTask staleTask = copyTask(task, TaskStatus.DRAFT_PACKET, task.rowVersion() + 1, now,
                    command.actorUserId());
            taskRepository.updateWithVersionOrThrow(staleTask, task.rowVersion());
            return new PacketPublicationResult(null, recheck, false, staleTask.status());
        }
        TaskStatus currentStatus = task.status();
        if (currentStatus != TaskStatus.WAITING_REVIEW && currentStatus != TaskStatus.RECHECKING_SCOPE
                && currentStatus != TaskStatus.READY_TO_PUBLISH) {
            throw new RepositoryStateViolationException("investigation task is not ready for publication");
        }
        InvestigationTask readyTask = currentStatus == TaskStatus.READY_TO_PUBLISH ? task
                : copyTask(task, TaskStatus.READY_TO_PUBLISH, task.rowVersion() + 1, now, command.actorUserId());
        if (readyTask != task) {
            taskRepository.updateWithVersionOrThrow(readyTask, task.rowVersion());
        }
        int revisionNo = revisionRepository.findPublishedByTaskId(command.taskId()).map(PacketRevision::revisionNo)
                .orElse(0) + 1;
        PacketRevision revision = new PacketRevision(command.publicationId(), command.taskId(), revisionNo,
                command.draftId(), new VersionedJson("packet-v1", canonical.value()), command.htmlSnapshot(),
                command.contentDigest(), now, new VersionedJson("runtime-v1", CanonicalJson.of(command.sourceVersions()).value()),
                new VersionedJson("coverage-v1", CanonicalJson.of(command.coverageSummary()).value()), command.actorUserId(),
                now, revisionRepository.findPublishedByTaskId(command.taskId()).map(PacketRevision::packetRevisionId).orElse(null),
                command.actorUserId(), now, command.actorUserId(), now);
        InvestigationTask publishedTask = copyTask(readyTask, TaskStatus.PUBLISHED, readyTask.rowVersion() + 1, now,
                command.actorUserId());
        AuditEvent audit = auditEvent(command, revision, now);
        OutboxEvent outbox = outboxEvent(command, revision, now);
        PacketRevision stored = transactionalPublisher.publish(new PublishTransactionCommand(command.taskId(), publishedTask,
                revision, audit, outbox));
        return new PacketPublicationResult(stored, recheck, true, publishedTask.status());
    }

    private AuditEvent auditEvent(PacketPublicationCommand command, PacketRevision revision, Instant now) {
        ObjectNode payload = JSON_MAPPER.createObjectNode();
        payload.put("revisionNo", revision.revisionNo());
        payload.put("contentDigest", revision.contentDigest());
        payload.put("scopeRevisionId", command.scopeRevisionId().toString());
        return AuditEvent.create(UuidGenerator.newUuid(), command.taskId(), command.actorUserId(), "PACKET_PUBLISHED",
                "packet_revision", revision.packetRevisionId().toString(), new VersionedJson("audit-payload-v1", payload.toString()), now,
                auditEventRepository.findLatestByTaskId(command.taskId()).map(AuditEvent::eventDigest).orElse(null),
                command.actorUserId(), now, command.actorUserId(), now);
    }

    private static OutboxEvent outboxEvent(PacketPublicationCommand command, PacketRevision revision, Instant now) {
        ObjectNode payload = JSON_MAPPER.createObjectNode();
        payload.put("taskId", command.taskId().toString());
        payload.put("packetRevisionId", revision.packetRevisionId().toString());
        payload.put("contentDigest", revision.contentDigest());
        return new OutboxEvent(UuidGenerator.newUuid(), "packet_revision", revision.packetRevisionId().toString(),
                "PACKET_PUBLISHED", command.publicationId().toString(), new VersionedJson("packet-published-v1", payload.toString()),
                now, null, 0, command.actorUserId(), command.actorUserId(), now);
    }

    private static InvestigationTask copyTask(InvestigationTask task, TaskStatus status, long rowVersion, Instant now,
            UUID actorUserId) {
        return new InvestigationTask(task.taskId(), task.title(), task.originalQuestion(), task.initiatorUserId(), status,
                task.runtimeBundleId(), task.currentScopeRevisionId(), task.currentStepId(), task.createdAt(), now,
                rowVersion, task.closedAt(), task.createdBy(), actorUserId);
    }

    private static void requireCommand(PacketPublicationCommand command) {
        if (command == null || command.taskId() == null || command.draftId() == null || command.publicationId() == null
                || command.actorUserId() == null || command.contentDigest() == null || command.scopeRevisionId() == null
                || command.runtimeBundleId() == null || command.canonicalJson() == null || command.htmlSnapshot() == null
                || command.sourceVersions() == null || command.coverageSummary() == null || command.baseline() == null
                || command.current() == null) {
            throw new IllegalArgumentException("publication command is incomplete");
        }
    }
}
