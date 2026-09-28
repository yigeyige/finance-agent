package cn.com.crc.ems.finance.agent.packet.application;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.packet.domain.Handoff;
import cn.com.crc.ems.finance.agent.packet.domain.PacketRevision;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryStateViolationException;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionCommand;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;

/** 编排发布决策包的只读查看、交接和调查关闭生命周期。 */
@Service
public class PacketLifecycleService {

    private final InvestigationTaskRepository taskRepository;
    private final PacketRevisionRepository revisionRepository;
    private final HandoffRepository handoffRepository;
    private final TaskStateTransitionService stateTransitionService;
    private final TaskAuditEventAppender auditEventAppender;
    private final Clock applicationClock;

    public PacketLifecycleService(InvestigationTaskRepository taskRepository,
            PacketRevisionRepository revisionRepository, HandoffRepository handoffRepository,
            TaskStateTransitionService stateTransitionService, TaskAuditEventAppender auditEventAppender,
            Clock applicationClock) {
        this.taskRepository = taskRepository;
        this.revisionRepository = revisionRepository;
        this.handoffRepository = handoffRepository;
        this.stateTransitionService = stateTransitionService;
        this.auditEventAppender = auditEventAppender;
        this.applicationClock = applicationClock;
    }

    /** 查询任务当前最新的已发布修订，供 OPERATOR 与 VIEWER 共用。 */
    @Transactional(readOnly = true)
    public PacketPublishedView publishedView(UUID taskId) {
        InvestigationTask task = findTask(taskId);
        PacketRevision revision = revisionRepository.findPublishedByTaskId(taskId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("published packet revision not found"));
        return new PacketPublishedView(taskId, revision, task.status(), task.closedAt(),
                handoffRepository.findByPacketRevisionId(revision.packetRevisionId()).orElse(null),
                limitations(revision));
    }

    /** 追加一次已发布修订交接并推进任务状态；重复请求返回已有交接。 */
    @Transactional
    public PacketHandoffResult handoff(PacketHandoffCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("handoff command is required");
        }
        InvestigationTask task = taskRepository.findByIdForUpdate(command.taskId()).orElseThrow(
                () -> new RepositoryEntityNotFoundException("investigation task not found"));
        PacketRevision revision = requireCurrentRevision(command.taskId(), command.packetRevisionId());
        Handoff existing = handoffRepository.findByPacketRevisionId(revision.packetRevisionId()).orElse(null);
        if (existing != null) {
            if (task.status() != TaskStatus.HANDED_OFF && task.status() != TaskStatus.CLOSED) {
                throw new RepositoryStateViolationException("handoff exists but task state is inconsistent");
            }
            return new PacketHandoffResult(task, existing);
        }
        if (task.status() != TaskStatus.PUBLISHED) {
            throw new RepositoryStateViolationException("only published task can be handed off");
        }
        Instant now = Instant.now(applicationClock);
        Handoff handoff = new Handoff(UuidGenerator.newUuid(), revision.packetRevisionId(), command.ownerReference().trim(),
                normalize(command.externalProcessReference()), command.downstreamStatus().trim(), command.actorUserId(), now,
                command.actorUserId(), now, command.actorUserId(), now);
        handoffRepository.insert(handoff);
        var transition = stateTransitionService.transition(new TaskStateTransitionCommand(command.taskId(),
                TaskStatus.HANDED_OFF, "packet-handoff", command.actorUserId(), now, true,
                task.currentScopeRevisionId()));
        auditEventAppender.append(UuidGenerator.newUuid(), command.taskId(), command.actorUserId(),
                "PACKET_HANDED_OFF", "packet_revision", revision.packetRevisionId().toString(), now, payload -> {
                    payload.put("handoffId", handoff.handoffId().toString());
                    payload.put("ownerReferenceDigest", Sha256Digest.hex(handoff.ownerReference()));
                    payload.put("externalProcessReferenceDigest", digestOrNull(handoff.externalProcessReference()));
                    payload.put("downstreamStatus", handoff.downstreamStatus());
                });
        return new PacketHandoffResult(transition.task(), handoff);
    }

    /** 要求已有责任引用后关闭调查，并明确整改完成状态仍未确认。 */
    @Transactional
    public TaskClosureResult close(TaskClosureCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("closure command is required");
        }
        InvestigationTask task = taskRepository.findByIdForUpdate(command.taskId()).orElseThrow(
                () -> new RepositoryEntityNotFoundException("investigation task not found"));
        PacketRevision revision = revisionRepository.findPublishedByTaskId(command.taskId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("published packet revision not found"));
        Handoff handoff = handoffRepository.findByPacketRevisionId(revision.packetRevisionId()).orElseThrow(
                () -> new RepositoryStateViolationException("owner reference is required before closure"));
        if (handoff.ownerReference() == null || handoff.ownerReference().isBlank()) {
            throw new RepositoryStateViolationException("owner reference is required before closure");
        }
        if (task.status() == TaskStatus.CLOSED) {
            return new TaskClosureResult(task, handoff, task.closedAt(), false);
        }
        if (task.status() != TaskStatus.HANDED_OFF) {
            throw new RepositoryStateViolationException("only handed-off task can be closed");
        }
        Instant now = Instant.now(applicationClock);
        var transition = stateTransitionService.transition(new TaskStateTransitionCommand(command.taskId(),
                TaskStatus.CLOSED, "investigation-closed", command.actorUserId(), now, true,
                task.currentScopeRevisionId()));
        auditEventAppender.append(UuidGenerator.newUuid(), command.taskId(), command.actorUserId(), "TASK_CLOSED",
                "packet_revision", revision.packetRevisionId().toString(), now, payload -> {
                    payload.put("handoffId", handoff.handoffId().toString());
                    payload.put("closureReasonDigest", Sha256Digest.hex(command.closureReason().trim()));
                    payload.put("remediationComplete", false);
                });
        return new TaskClosureResult(transition.task(), handoff, now, false);
    }

    private InvestigationTask findTask(UUID taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
    }

    private PacketRevision requireCurrentRevision(UUID taskId, UUID revisionId) {
        PacketRevision revision = revisionRepository.findById(revisionId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("packet revision not found"));
        PacketRevision current = revisionRepository.findPublishedByTaskId(taskId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("published packet revision not found"));
        if (!revision.taskId().equals(taskId) || !current.packetRevisionId().equals(revisionId)) {
            throw new RepositoryStateViolationException("packet revision is not the current published revision");
        }
        return revision;
    }

    private static String limitations(PacketRevision revision) {
        JsonNode node = CanonicalJsonCodec.parse(revision.coverageSummary().json()).get("limitations");
        if (node == null || node.isMissingNode() || node.isNull()) {
            return "发布摘要未声明限制";
        }
        return node.isTextual() ? node.textValue() : node.toString();
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String digestOrNull(String value) {
        return value == null ? null : Sha256Digest.hex(value);
    }
}
