package cn.com.crc.ems.finance.agent.evidence.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.com.crc.ems.finance.agent.evidence.domain.CoverageCompleteness;
import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;
import cn.com.crc.ems.finance.agent.evidence.domain.FactSnapshot;
import cn.com.crc.ems.finance.agent.evidence.domain.FindingEvidence;
import cn.com.crc.ems.finance.agent.rules.application.ApprovedSnapshot;
import cn.com.crc.ems.finance.agent.rules.application.PersonnelReferenceRule;
import cn.com.crc.ems.finance.agent.rules.application.RuleEvaluationContext;
import cn.com.crc.ems.finance.agent.rules.application.RuleEvaluationService;
import cn.com.crc.ems.finance.agent.rules.application.RuleInput;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.source.application.SourceQuery;
import cn.com.crc.ems.finance.agent.source.application.SyntheticScenario;
import cn.com.crc.ems.finance.agent.source.infrastructure.SyntheticConfigSourceAdapter;
import cn.com.crc.ems.finance.agent.task.application.ExecutionPermit;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.InvestigationStepRepository;
import cn.com.crc.ems.finance.agent.task.application.StepApprovalRepository;
import cn.com.crc.ems.finance.agent.task.application.StepExecutionGuard;
import cn.com.crc.ems.finance.agent.task.application.StepExecutionRequest;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationService;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationStartCommand;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationStartResult;
import cn.com.crc.ems.finance.agent.task.domain.ApprovalStatus;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.StepStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocationKey;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskLeaseService;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionCommand;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 *
 * @Description: 仅在本地合成场景中编排已审批步骤、固定来源和最小证据链。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-30 21:30:00
 */
@Service
public class SyntheticEvidenceWorkerService {

    private static final String TEMPLATE = "config.employee.references.v1";
    private static final String FIELD_POLICY = "fields-v1";
    private static final String QUERY_TEMPLATE = "query-v1";
    private static final String SCHEMA_VERSION = "snapshot-v1";
    private static final String WORKER_INSTANCE = "local-synthetic-evidence-worker";

    private final TaskLeaseService leaseService;
    private final InvestigationTaskRepository taskRepository;
    private final InvestigationStepRepository stepRepository;
    private final StepApprovalRepository approvalRepository;
    private final StepExecutionGuard executionGuard;
    private final ToolInvocationService invocationService;
    private final SyntheticConfigSourceAdapter sourceAdapter;
    private final FactSnapshotRepository snapshotRepository;
    private final CoverageRecordRepository coverageRepository;
    private final FindingRepository findingRepository;
    private final FindingEvidenceRepository findingEvidenceRepository;
    private final ComputationWitnessRepository witnessRepository;
    private final CoverageRecordFactory coverageFactory;
    private final RuleEvaluationService ruleEvaluationService;
    private final TaskStateTransitionService stateTransitionService;
    private final TaskAuditEventAppender auditEventAppender;
    private final Clock applicationClock;

    public SyntheticEvidenceWorkerService(TaskLeaseService leaseService, InvestigationTaskRepository taskRepository,
            InvestigationStepRepository stepRepository,
            StepApprovalRepository approvalRepository,
            StepExecutionGuard executionGuard, ToolInvocationService invocationService,
            SyntheticConfigSourceAdapter sourceAdapter, FactSnapshotRepository snapshotRepository,
            CoverageRecordRepository coverageRepository, FindingRepository findingRepository,
            FindingEvidenceRepository findingEvidenceRepository, ComputationWitnessRepository witnessRepository,
            CoverageRecordFactory coverageFactory, RuleEvaluationService ruleEvaluationService,
            TaskStateTransitionService stateTransitionService, TaskAuditEventAppender auditEventAppender,
            Clock applicationClock) {
        this.leaseService = leaseService;
        this.taskRepository = taskRepository;
        this.stepRepository = stepRepository;
        this.approvalRepository = approvalRepository;
        this.executionGuard = executionGuard;
        this.invocationService = invocationService;
        this.sourceAdapter = sourceAdapter;
        this.snapshotRepository = snapshotRepository;
        this.coverageRepository = coverageRepository;
        this.findingRepository = findingRepository;
        this.findingEvidenceRepository = findingEvidenceRepository;
        this.witnessRepository = witnessRepository;
        this.coverageFactory = coverageFactory;
        this.ruleEvaluationService = ruleEvaluationService;
        this.stateTransitionService = stateTransitionService;
        this.auditEventAppender = auditEventAppender;
        this.applicationClock = applicationClock;
    }

    /**
     *
     * @description: 对指定任务的当前已审批步骤执行固定合成来源，并持久化最小确定性证据链。
     *
     * @param: taskId 任务标识
     * @param: actorId 当前操作人
     * @param: externalIdempotencyKey HTTP 幂等键
     * @return: 不含原始来源内容的执行摘要
     * @author: yigeyige99
     */
    @Transactional
    public SyntheticEvidenceWorkerResult run(UUID taskId, UUID actorId, String externalIdempotencyKey) {
        if (taskId == null || actorId == null || externalIdempotencyKey == null || externalIdempotencyKey.isBlank()) {
            throw new IllegalArgumentException("task, actor and idempotency key are required");
        }
        Instant now = Instant.now(applicationClock);
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("investigation task not found"));
        if (task.currentStepId() == null) {
            throw new IllegalStateException("task has no current step");
        }
        var approval = approvalRepository.findByStepId(task.currentStepId()).stream()
                .filter(value -> value.status() == ApprovalStatus.ACTIVE).max(java.util.Comparator.comparingInt(
                        cn.com.crc.ems.finance.agent.task.domain.StepApproval::revisionNo))
                .orElseThrow(() -> new IllegalStateException("current step has no active approval"));
        requireSyntheticBinding(approval.fieldPolicyVersion(), approval.queryTemplateVersion());
        int invocationNo = Math.max(1, externalIdempotencyKey.hashCode() & Integer.MAX_VALUE);
        ToolInvocationKey key = new ToolInvocationKey(taskId, task.currentStepId(), approval.revisionNo(), invocationNo);
        ToolInvocationStartResult started = invocationService.startIntent(new ToolInvocationStartCommand(key,
                approval.approvalId(), "synthetic-evidence", Sha256Digest.hex(TEMPLATE + "|" + externalIdempotencyKey),
                actorId, now));
        if (started.action() == ToolInvocationStartResult.Action.REPLAY) {
            return new SyntheticEvidenceWorkerResult(taskId, task.currentStepId(), started.invocation().invocationId(), 0,
                    null, started.invocation().responseDigest(), true);
        }
        if (started.action() != ToolInvocationStartResult.Action.EXECUTE) {
            throw new IllegalStateException("synthetic execution is already in progress or requires reconciliation");
        }
        var claim = leaseService.claim(taskId, WORKER_INSTANCE, actorId, now, Duration.ofMinutes(2));
        try {
            if (!claim.step().stepId().equals(key.stepId())) {
                throw new IllegalStateException("claimed step does not match invocation intent");
            }
            invocationService.markRunning(key.value(), actorId, now);
            ExecutionPermit permit = executionGuard.issuePermit(new StepExecutionRequest(taskId, claim.step().stepId(),
                    approval.approvalId(), approval.scopeRevisionId(), approval.sourceBindingId(), approval.goalHash(),
                    approval.fieldPolicyVersion(), approval.queryTemplateVersion(), key.value(), now));
            if (!executionGuard.isValid(permit, Instant.now(applicationClock))) {
                throw new IllegalStateException("synthetic execution permit is invalid");
            }
            return writeEvidence(taskId, claim.step().stepId(), approval, started, actorId, key.value(), now);
        } finally {
            leaseService.release(claim.lease(), Instant.now(applicationClock), actorId);
        }
    }

    private SyntheticEvidenceWorkerResult writeEvidence(UUID taskId, UUID stepId,
            cn.com.crc.ems.finance.agent.task.domain.StepApproval approval, ToolInvocationStartResult started,
            UUID actorId, String idempotencyKey, Instant now) {
        var page = sourceAdapter.query(new SourceQuery(TEMPLATE, Map.of("page", "1", "size", "100")),
                SyntheticScenario.BASELINE, 0);
        List<FactSnapshot> snapshots = page.records().stream().map(record -> snapshot(taskId, stepId, approval, record,
                page.sourceVersion(), now, actorId)).toList();
        snapshots.forEach(snapshotRepository::insert);
        coverageRepository.insert(coverageFactory.create(UuidGenerator.newUuid(), taskId, stepId,
                approval.scopeRevisionId(), approval.queryTemplateVersion(), Sha256Digest.hex(TEMPLATE), TEMPLATE,
                new CoverageEnumerationResult<>(List.of(new CoveragePage<>(page.page(), page.records(),
                        page.expectedTotal(), page.hasNext(), page.hasNext() ? "page-2" : null,
                        page.sourceVersion(), "workflow.reference|role.reference")), page.records(), page.expectedTotal(),
                        page.records().size(), List.of("workflow.reference", "role.reference"), List.of(), List.of(),
                        page.sourceVersion(), page.resultDigest(), CoverageCompleteness.COMPLETE), now, actorId));
        var evaluated = ruleEvaluationService.evaluate(new PersonnelReferenceRule(), new RuleEvaluationContext(
                UuidGenerator.newUuid(), taskId, approval.scopeRevisionId(), now, actorId), new RuleInput(snapshots.stream()
                        .map(this::approvedSnapshot).toList()));
        findingRepository.insert(evaluated.finding());
        snapshots.forEach(snapshot -> findingEvidenceRepository.insert(new FindingEvidence(evaluated.finding().findingId(),
                snapshot.snapshotId(), "SUPPORTS", actorId, now, actorId, now)));
        witnessRepository.insert(evaluated.witness());
        String digest = Sha256Digest.hex(page.resultDigest() + "|" + evaluated.witness().outputDigest());
        invocationService.complete(idempotencyKey, InvocationStatus.SUCCEEDED, digest, null, actorId, now);
        if (stepRepository.updateStatus(stepId, StepStatus.SUCCEEDED, actorId, now) != 1) {
            throw new IllegalStateException("step completion did not affect one row");
        }
        stateTransitionService.transition(new TaskStateTransitionCommand(taskId, TaskStatus.ANALYZING,
                "synthetic evidence captured", actorId, now, true));
        stateTransitionService.transition(new TaskStateTransitionCommand(taskId, TaskStatus.DRAFT_PACKET,
                "synthetic evidence ready for packet", actorId, now, true));
        auditEventAppender.append(UuidGenerator.newUuid(), taskId, actorId, "SYNTHETIC_EVIDENCE_CAPTURED", "step",
                stepId.toString(), now, payload -> { payload.put("snapshotCount", snapshots.size());
                    payload.put("findingId", evaluated.finding().findingId().toString()); payload.put("resultDigest", digest); });
        return new SyntheticEvidenceWorkerResult(taskId, stepId, started.invocation().invocationId(), snapshots.size(),
                evaluated.finding().findingId(), digest, false);
    }

    private FactSnapshot snapshot(UUID taskId, UUID stepId, cn.com.crc.ems.finance.agent.task.domain.StepApproval approval,
            Map<String, Object> record, String sourceVersion, Instant now, UUID actorId) {
        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        payload.put("reference.id", String.valueOf(record.get("syntheticReferenceId")));
        payload.put("reference.classification", String.valueOf(record.get("classification")));
        String allowedFields = CanonicalJsonCodec.canonicalize(payload);
        return new FactSnapshot(UuidGenerator.newUuid(), taskId, stepId, approval.sourceBindingId(), "synthetic_reference",
                String.valueOf(record.get("syntheticReferenceId")), allowedFields, now, null, null,
                effectiveClass(String.valueOf(record.get("classification"))), sourceVersion, Sha256Digest.hex(allowedFields),
                approval.fieldPolicyVersion(), SCHEMA_VERSION, actorId, now, actorId, now);
    }

    private ApprovedSnapshot approvedSnapshot(FactSnapshot snapshot) {
        return new ApprovedSnapshot(snapshot.snapshotId(), CanonicalJsonCodec.parse(snapshot.allowedFields()),
                java.util.Set.of("reference.id", "reference.classification"));
    }

    private static EffectiveClass effectiveClass(String value) {
        return switch (value) { case "CURRENT" -> EffectiveClass.CURRENT_EFFECTIVE;
            case "FUTURE" -> EffectiveClass.FUTURE_SCHEDULED; default -> EffectiveClass.UNDETERMINED; };
    }

    private static void requireSyntheticBinding(String fieldPolicyVersion, String queryTemplateVersion) {
        if (!FIELD_POLICY.equals(fieldPolicyVersion) || !QUERY_TEMPLATE.equals(queryTemplateVersion)) {
            throw new IllegalStateException("synthetic execution binding is not registered");
        }
    }
}
