package cn.com.crc.ems.finance.agent.task.infrastructure;

import cn.com.crc.ems.finance.agent.task.domain.ApprovalStatus;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;
import cn.com.crc.ems.finance.agent.task.domain.StepStatus;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;
import cn.com.crc.ems.finance.agent.task.domain.TaskPremise;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.InvestigationStepEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.InvestigationTaskEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.ScopeRevisionEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.StepApprovalEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.TaskLeaseEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.TaskPremiseEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.ToolInvocationEntity;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/** task 持久化实体与领域快照之间的显式映射。 */
final class MybatisTaskMappings {

    private MybatisTaskMappings() {
    }

    static InvestigationTask toDomain(InvestigationTaskEntity e) {
        return new InvestigationTask(PersistenceUuid.toDomain(e.taskId), e.title, e.originalQuestion, e.initiatorUserId,
                TaskStatus.valueOf(e.status), e.runtimeBundleId, e.currentScopeRevisionId, e.currentStepId,
                e.createdAt, e.updatedAt, e.rowVersion, e.closedAt, e.createdBy, e.updatedBy);
    }

    static InvestigationTaskEntity toEntity(InvestigationTask d) {
        InvestigationTaskEntity e = new InvestigationTaskEntity();
        e.taskId = PersistenceUuid.toStorage(d.taskId()); e.title = d.title(); e.originalQuestion = d.originalQuestion();
        e.initiatorUserId = d.initiatorUserId(); e.status = d.status().name(); e.runtimeBundleId = d.runtimeBundleId();
        e.currentScopeRevisionId = d.currentScopeRevisionId(); e.currentStepId = d.currentStepId();
        e.createdAt = d.createdAt(); e.updatedAt = d.updatedAt(); e.rowVersion = d.rowVersion(); e.closedAt = d.closedAt();
        e.createdBy = d.createdBy(); e.updatedBy = d.updatedBy(); return e;
    }

    static ScopeRevision toDomain(ScopeRevisionEntity e) {
        return new ScopeRevision(PersistenceUuid.toDomain(e.scopeRevisionId), e.taskId, e.revisionNo, e.systemCode,
                json(e.objectSelector, e.schemaVersion), json(e.effectiveTimeRange, e.schemaVersion),
                json(e.includedPaths, e.schemaVersion), json(e.excludedPaths, e.schemaVersion),
                json(e.allowedReadFields, e.schemaVersion), json(e.allowedRetainFields, e.schemaVersion),
                json(e.allowedModelFields, e.schemaVersion), e.confirmedBy, e.confirmedAt, e.scopeDigest,
                e.schemaVersion, e.createdBy, e.createdAt, e.updatedBy, e.updatedAt);
    }

    static ScopeRevisionEntity toEntity(ScopeRevision d) {
        ScopeRevisionEntity e = new ScopeRevisionEntity();
        e.scopeRevisionId = PersistenceUuid.toStorage(d.scopeRevisionId()); e.taskId = d.taskId(); e.revisionNo = d.revisionNo(); e.systemCode = d.systemCode();
        e.objectSelector = d.objectSelector().json(); e.effectiveTimeRange = d.effectiveTimeRange().json();
        e.includedPaths = d.includedPaths().json(); e.excludedPaths = d.excludedPaths().json();
        e.allowedReadFields = d.allowedReadFields().json(); e.allowedRetainFields = d.allowedRetainFields().json();
        e.allowedModelFields = d.allowedModelFields().json(); e.confirmedBy = d.confirmedBy(); e.confirmedAt = d.confirmedAt();
        e.scopeDigest = d.scopeDigest(); e.schemaVersion = d.schemaVersion(); e.createdBy = d.createdBy(); e.createdAt = d.createdAt();
        e.updatedBy = d.updatedBy(); e.updatedAt = d.updatedAt(); return e;
    }

    static TaskPremise toDomain(TaskPremiseEntity e) {
        return new TaskPremise(PersistenceUuid.toDomain(e.premiseId), e.taskId, e.premiseType, e.subjectKey, e.statement, e.providedBy,
                e.sourceDescription, e.providedAt, Boolean.TRUE.equals(e.independentlyVerified), e.createdBy, e.createdAt, e.updatedBy, e.updatedAt);
    }

    static TaskPremiseEntity toEntity(TaskPremise d) {
        TaskPremiseEntity e = new TaskPremiseEntity(); e.premiseId = PersistenceUuid.toStorage(d.premiseId()); e.taskId = d.taskId(); e.premiseType = d.premiseType();
        e.subjectKey = d.subjectKey(); e.statement = d.statement(); e.providedBy = d.providedBy(); e.sourceDescription = d.sourceDescription();
        e.providedAt = d.providedAt(); e.independentlyVerified = d.independentlyVerified(); e.createdBy = d.createdBy(); e.createdAt = d.createdAt();
        e.updatedBy = d.updatedBy(); e.updatedAt = d.updatedAt(); return e;
    }

    static InvestigationStep toDomain(InvestigationStepEntity e) {
        return new InvestigationStep(PersistenceUuid.toDomain(e.stepId), e.taskId, e.sequenceNo, e.goal, Boolean.TRUE.equals(e.recommended),
                StepStatus.valueOf(e.status), e.createdAt, e.completedAt, e.createdBy, e.updatedAt, e.updatedBy);
    }

    static InvestigationStepEntity toEntity(InvestigationStep d) {
        InvestigationStepEntity e = new InvestigationStepEntity(); e.stepId = PersistenceUuid.toStorage(d.stepId()); e.taskId = d.taskId(); e.sequenceNo = d.sequenceNo();
        e.goal = d.goal(); e.recommended = d.recommended(); e.status = d.status().name(); e.createdAt = d.createdAt(); e.completedAt = d.completedAt();
        e.createdBy = d.createdBy(); e.updatedAt = d.updatedAt(); e.updatedBy = d.updatedBy(); return e;
    }

    static StepApproval toDomain(StepApprovalEntity e) {
        return new StepApproval(PersistenceUuid.toDomain(e.approvalId), e.stepId, e.revisionNo, e.approvedBy, e.approvedAt, e.expiresAt, e.goalHash, e.sourceBindingId,
                e.scopeRevisionId, e.fieldPolicyVersion, e.queryTemplateVersion, e.maxCalls, e.usedCalls, e.approvalDigest,
                ApprovalStatus.valueOf(e.status), e.createdBy, e.createdAt, e.updatedBy, e.updatedAt);
    }

    static StepApprovalEntity toEntity(StepApproval d) {
        StepApprovalEntity e = new StepApprovalEntity(); e.approvalId = PersistenceUuid.toStorage(d.approvalId()); e.stepId = d.stepId(); e.revisionNo = d.revisionNo();
        e.approvedBy = d.approvedBy(); e.approvedAt = d.approvedAt(); e.expiresAt = d.expiresAt(); e.goalHash = d.goalHash();
        e.sourceBindingId = d.sourceBindingId(); e.scopeRevisionId = d.scopeRevisionId(); e.fieldPolicyVersion = d.fieldPolicyVersion();
        e.queryTemplateVersion = d.queryTemplateVersion(); e.maxCalls = d.maxCalls(); e.usedCalls = d.usedCalls(); e.approvalDigest = d.approvalDigest();
        e.status = d.status().name(); e.createdBy = d.createdBy(); e.createdAt = d.createdAt(); e.updatedBy = d.updatedBy(); e.updatedAt = d.updatedAt(); return e;
    }

    static TaskLease toDomain(TaskLeaseEntity e) {
        return new TaskLease(PersistenceUuid.toDomain(e.leaseId), e.taskId, e.stepId, e.ownerInstance, e.acquiredAt, e.heartbeatAt, e.expiresAt,
                e.releasedAt, e.createdBy, e.createdAt, e.updatedBy, e.updatedAt);
    }

    static TaskLeaseEntity toEntity(TaskLease d) {
        TaskLeaseEntity e = new TaskLeaseEntity(); e.leaseId = PersistenceUuid.toStorage(d.leaseId()); e.taskId = d.taskId(); e.stepId = d.stepId();
        e.ownerInstance = d.ownerInstance(); e.acquiredAt = d.acquiredAt(); e.heartbeatAt = d.heartbeatAt(); e.expiresAt = d.expiresAt();
        e.releasedAt = d.releasedAt(); e.createdBy = d.createdBy(); e.createdAt = d.createdAt(); e.updatedBy = d.updatedBy(); e.updatedAt = d.updatedAt(); return e;
    }

    static ToolInvocation toDomain(ToolInvocationEntity e) {
        return new ToolInvocation(PersistenceUuid.toDomain(e.invocationId), e.taskId, e.stepId, e.approvalId, e.toolCode, e.idempotencyKey,
                e.requestDigest, e.responseDigest, InvocationStatus.valueOf(e.status), e.startedAt, e.completedAt, e.errorCode,
                e.createdBy, e.createdAt, e.updatedBy, e.updatedAt);
    }

    static ToolInvocationEntity toEntity(ToolInvocation d) {
        ToolInvocationEntity e = new ToolInvocationEntity(); e.invocationId = PersistenceUuid.toStorage(d.invocationId()); e.taskId = d.taskId(); e.stepId = d.stepId();
        e.approvalId = d.approvalId(); e.toolCode = d.toolCode(); e.idempotencyKey = d.idempotencyKey(); e.requestDigest = d.requestDigest();
        e.responseDigest = d.responseDigest(); e.status = d.status().name(); e.startedAt = d.startedAt(); e.completedAt = d.completedAt();
        e.errorCode = d.errorCode(); e.createdBy = d.createdBy(); e.createdAt = d.createdAt(); e.updatedBy = d.updatedBy(); e.updatedAt = d.updatedAt(); return e;
    }

    private static VersionedJson json(String value, String schemaVersion) {
        return new VersionedJson(schemaVersion, value);
    }
}
