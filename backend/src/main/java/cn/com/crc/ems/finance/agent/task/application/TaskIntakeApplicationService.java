package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJson;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.domain.InvalidTaskTransitionException;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;
import cn.com.crc.ems.finance.agent.task.domain.TaskPremise;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateMachine;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;
import cn.com.crc.ems.finance.agent.packet.application.PacketDraftRepository;
import cn.com.crc.ems.finance.agent.task.application.StepApprovalRepository;

/** 任务发起、运维前提和范围确认的事务应用边界。 */
@Service
public class TaskIntakeApplicationService {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
    private static final Set<TaskStatus> SCOPE_CONFIRMABLE = Set.of(TaskStatus.DRAFT,
            TaskStatus.WAITING_SCOPE_CONFIRMATION, TaskStatus.READY);

    private final InvestigationTaskRepository taskRepository;
    private final TaskPremiseRepository premiseRepository;
    private final ScopeRevisionRepository scopeRevisionRepository;
    private final TaskStateTransitionService stateTransitionService;
    private final TaskAuditEventAppender auditEventAppender;
    private final StepApprovalRepository stepApprovalRepository;
    private final PacketDraftRepository packetDraftRepository;

    public TaskIntakeApplicationService(InvestigationTaskRepository taskRepository,
            TaskPremiseRepository premiseRepository, ScopeRevisionRepository scopeRevisionRepository,
            TaskStateTransitionService stateTransitionService, TaskAuditEventAppender auditEventAppender) {
        this(taskRepository, premiseRepository, scopeRevisionRepository, stateTransitionService, auditEventAppender,
                null, null);
    }

    @Autowired
    public TaskIntakeApplicationService(InvestigationTaskRepository taskRepository,
            TaskPremiseRepository premiseRepository, ScopeRevisionRepository scopeRevisionRepository,
            TaskStateTransitionService stateTransitionService, TaskAuditEventAppender auditEventAppender,
            StepApprovalRepository stepApprovalRepository, PacketDraftRepository packetDraftRepository) {
        this.taskRepository = taskRepository;
        this.premiseRepository = premiseRepository;
        this.scopeRevisionRepository = scopeRevisionRepository;
        this.stateTransitionService = stateTransitionService;
        this.auditEventAppender = auditEventAppender;
        this.stepApprovalRepository = stepApprovalRepository;
        this.packetDraftRepository = packetDraftRepository;
    }

    @Transactional
    public InvestigationTask createTask(CreateTaskDraftCommand command) {
        requireCommand(command);
        UUID taskId = UuidGenerator.newUuid();
        InvestigationTask task = new InvestigationTask(taskId, command.title(), command.originalQuestion(),
                command.initiatorUserId(), TaskStatus.DRAFT, command.runtimeBundleId(), null, null,
                command.occurredAt(), command.occurredAt(), 0, null, command.actorUserId(), command.actorUserId());
        taskRepository.insert(task);
        auditEventAppender.append(UuidGenerator.newUuid(), taskId, command.actorUserId(), "TASK_CREATED", "task",
                taskId.toString(), command.occurredAt(), payload -> {
                    payload.put("status", TaskStatus.DRAFT.name());
                    payload.put("titleDigest", Sha256Digest.hex(command.title()));
                    payload.put("questionDigest", Sha256Digest.hex(command.originalQuestion()));
                    payload.put("rowVersion", task.rowVersion());
                });
        return task;
    }

    @Transactional
    public TaskPremise addPremise(AddTaskPremiseCommand command) {
        requireCommand(command);
        InvestigationTask task = taskRepository.findByIdForUpdate(command.taskId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        if (task.status() == TaskStatus.CLOSED || task.status() == TaskStatus.CANCELLED) {
            throw new InvalidTaskTransitionException("terminal task cannot accept premises");
        }
        TaskPremise premise = new TaskPremise(UuidGenerator.newUuid(), task.taskId(), command.premiseType(),
                command.subjectKey(), command.statement(), command.providedBy(), command.sourceDescription(),
                command.occurredAt(), false, command.providedBy(), command.occurredAt(), command.providedBy(),
                command.occurredAt());
        premiseRepository.insert(premise);
        auditEventAppender.append(UuidGenerator.newUuid(), task.taskId(), command.providedBy(), "TASK_PREMISE_ADDED",
                "task_premise", premise.premiseId().toString(), command.occurredAt(), payload -> {
                    payload.put("premiseType", premise.premiseType());
                    payload.put("subjectKeyDigest", Sha256Digest.hex(premise.subjectKey()));
                    payload.put("statementDigest", Sha256Digest.hex(premise.statement()));
                    payload.put("sourceDigest", Sha256Digest.hex(premise.sourceDescription()));
                    payload.put("independentlyVerified", false);
                });
        return premise;
    }

    @Transactional(readOnly = true)
    public InvestigationTask findTask(UUID taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
    }

    @Transactional
    public ScopeConfirmationResult confirmScope(ConfirmScopeCommand command) {
        requireCommand(command);
        InvestigationTask current = taskRepository.findByIdForUpdate(command.taskId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        if (!SCOPE_CONFIRMABLE.contains(current.status())) {
            throw new InvalidTaskTransitionException("task is not ready for scope confirmation");
        }
        int revisionNo = scopeRevisionRepository.findLatestByTaskId(current.taskId())
                .map(existing -> existing.revisionNo() + 1).orElse(1);
        ScopeRevision revision = new ScopeRevision(UuidGenerator.newUuid(), current.taskId(), revisionNo,
                command.systemCode(), new VersionedJson(command.schemaVersion(), command.objectSelector()),
                new VersionedJson(command.schemaVersion(), command.effectiveTimeRange()),
                new VersionedJson(command.schemaVersion(), command.includedPaths()),
                new VersionedJson(command.schemaVersion(), command.excludedPaths()),
                new VersionedJson(command.schemaVersion(), command.allowedReadFields()),
                new VersionedJson(command.schemaVersion(), command.allowedRetainFields()),
                new VersionedJson(command.schemaVersion(), command.allowedModelFields()), command.confirmedBy(),
                command.occurredAt(), scopeDigest(command, revisionNo), command.schemaVersion(), command.confirmedBy(),
                command.occurredAt(), command.confirmedBy(), command.occurredAt());
        int staleApprovalCount = 0;
        int staleDraftCount = 0;
        if (current.currentScopeRevisionId() != null) {
            if (stepApprovalRepository != null) {
                staleApprovalCount = stepApprovalRepository.markStaleByScopeRevision(current.taskId(),
                        current.currentScopeRevisionId(), command.confirmedBy(), command.occurredAt());
            }
            if (packetDraftRepository != null) {
                staleDraftCount = packetDraftRepository.markStaleByScopeRevision(current.taskId(),
                        current.currentScopeRevisionId(), command.confirmedBy(), command.occurredAt());
            }
        }
        scopeRevisionRepository.insert(revision);

        InvestigationTask updated = current;
        if (updated.status() == TaskStatus.DRAFT || updated.status() == TaskStatus.READY) {
            updated = stateTransitionService.transition(new TaskStateTransitionCommand(updated.taskId(),
                    TaskStatus.WAITING_SCOPE_CONFIRMATION, "scope-confirmation-requested", command.confirmedBy(),
                    command.occurredAt(), false)).task();
        }
        updated = stateTransitionService.transition(new TaskStateTransitionCommand(updated.taskId(), TaskStatus.READY,
                "scope-confirmed", command.confirmedBy(), command.occurredAt(), false, revision.scopeRevisionId()))
                .task();
        InvestigationTask finalTask = updated;
        int finalStaleApprovalCount = staleApprovalCount;
        int finalStaleDraftCount = staleDraftCount;
        auditEventAppender.append(UuidGenerator.newUuid(), updated.taskId(), command.confirmedBy(),
                "SCOPE_CONFIRMED", "scope_revision", revision.scopeRevisionId().toString(), command.occurredAt(),
                payload -> {
                    payload.put("revisionNo", revision.revisionNo());
                    payload.put("scopeDigest", revision.scopeDigest());
                    payload.put("schemaVersion", revision.schemaVersion());
                    payload.put("rowVersion", finalTask.rowVersion());
                    payload.put("staleApprovalCount", finalStaleApprovalCount);
                    payload.put("staleDraftCount", finalStaleDraftCount);
                });
        return new ScopeConfirmationResult(finalTask, revision);
    }

    private static String scopeDigest(ConfirmScopeCommand command, int revisionNo) {
        ObjectNode payload = JSON_MAPPER.createObjectNode();
        payload.put("systemCode", command.systemCode());
        payload.put("revisionNo", revisionNo);
        payload.set("objectSelector", parse(command.objectSelector()));
        payload.set("effectiveTimeRange", parse(command.effectiveTimeRange()));
        payload.set("includedPaths", parse(command.includedPaths()));
        payload.set("excludedPaths", parse(command.excludedPaths()));
        payload.set("allowedReadFields", parse(command.allowedReadFields()));
        payload.set("allowedRetainFields", parse(command.allowedRetainFields()));
        payload.set("allowedModelFields", parse(command.allowedModelFields()));
        return CanonicalJson.of(payload.toString()).sha256();
    }

    private static com.fasterxml.jackson.databind.JsonNode parse(String value) {
        try {
            return JSON_MAPPER.readTree(value.trim());
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("scope JSON must be valid JSON", exception);
        }
    }

    private static void requireCommand(Object command) {
        if (command == null) {
            throw new IllegalArgumentException("command is required");
        }
    }
}
