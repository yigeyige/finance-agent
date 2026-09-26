package cn.com.crc.ems.finance.agent.evidence.infrastructure;

import cn.com.crc.ems.finance.agent.evidence.domain.ConfirmationDecision;
import cn.com.crc.ems.finance.agent.evidence.domain.ComputationWitness;
import cn.com.crc.ems.finance.agent.evidence.domain.CoverageCompleteness;
import cn.com.crc.ems.finance.agent.evidence.domain.CoverageRecord;
import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;
import cn.com.crc.ems.finance.agent.evidence.domain.FactSnapshot;
import cn.com.crc.ems.finance.agent.evidence.domain.Finding;
import cn.com.crc.ems.finance.agent.evidence.domain.FindingEvidence;
import cn.com.crc.ems.finance.agent.evidence.domain.FindingStatus;
import cn.com.crc.ems.finance.agent.evidence.domain.JudgmentMode;
import cn.com.crc.ems.finance.agent.evidence.domain.ProfessionalConfirmation;
import cn.com.crc.ems.finance.agent.evidence.domain.RecomputeStatus;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.ComputationWitnessEntity;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.CoverageRecordEntity;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.FactSnapshotEntity;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.FindingEntity;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.FindingEvidenceEntity;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.ProfessionalConfirmationEntity;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/** evidence 持久化实体与领域对象之间的显式映射。 */
final class MybatisEvidenceMappings {

    private MybatisEvidenceMappings() {
    }

    static FactSnapshot toDomain(FactSnapshotEntity e) {
        return new FactSnapshot(PersistenceUuid.toDomain(e.snapshotId), e.taskId, e.stepId, e.sourceBindingId,
                e.sourceObjectType, e.sourceObjectKey, e.allowedFields, e.checkedAt, e.effectiveFrom, e.effectiveTo,
                EffectiveClass.valueOf(e.effectiveClass), e.sourceVersion, e.resultDigest, e.fieldPolicyVersion,
                e.schemaVersion, e.createdBy, e.createdAt, e.updatedBy, e.updatedAt);
    }

    static FactSnapshotEntity toEntity(FactSnapshot d) {
        FactSnapshotEntity e = new FactSnapshotEntity();
        e.snapshotId = PersistenceUuid.toStorage(d.snapshotId()); e.taskId = d.taskId(); e.stepId = d.stepId();
        e.sourceBindingId = d.sourceBindingId(); e.sourceObjectType = d.sourceObjectType(); e.sourceObjectKey = d.sourceObjectKey();
        e.allowedFields = d.allowedFields(); e.checkedAt = d.checkedAt(); e.effectiveFrom = d.effectiveFrom(); e.effectiveTo = d.effectiveTo();
        e.effectiveClass = d.effectiveClass().name(); e.sourceVersion = d.sourceVersion(); e.resultDigest = d.resultDigest();
        e.fieldPolicyVersion = d.fieldPolicyVersion(); e.schemaVersion = d.schemaVersion(); e.createdBy = d.createdBy();
        e.createdAt = d.createdAt(); e.updatedBy = d.updatedBy(); e.updatedAt = d.updatedAt(); return e;
    }

    static CoverageRecord toDomain(CoverageRecordEntity e) {
        return new CoverageRecord(PersistenceUuid.toDomain(e.coverageId), e.taskId, e.stepId, e.scopeRevisionId,
                e.queryTemplateVersion, e.queryConditionDigest, e.enumerationMethod, e.pageCount, e.expectedTotal,
                e.observedTotal, e.traversedPaths, e.uncoveredPaths, e.failures, e.sourceWatermark, e.resultSetDigest,
                CoverageCompleteness.valueOf(e.completeness), e.checkedAt, e.createdBy, e.createdAt, e.updatedBy, e.updatedAt);
    }

    static CoverageRecordEntity toEntity(CoverageRecord d) {
        CoverageRecordEntity e = new CoverageRecordEntity();
        e.coverageId = PersistenceUuid.toStorage(d.coverageId()); e.taskId = d.taskId(); e.stepId = d.stepId();
        e.scopeRevisionId = d.scopeRevisionId(); e.queryTemplateVersion = d.queryTemplateVersion();
        e.queryConditionDigest = d.queryConditionDigest(); e.enumerationMethod = d.enumerationMethod(); e.pageCount = d.pageCount();
        e.expectedTotal = d.expectedTotal(); e.observedTotal = d.observedTotal(); e.traversedPaths = d.traversedPaths();
        e.uncoveredPaths = d.uncoveredPaths(); e.failures = d.failures(); e.sourceWatermark = d.sourceWatermark();
        e.resultSetDigest = d.resultSetDigest(); e.completeness = d.completeness().name(); e.checkedAt = d.checkedAt();
        e.createdBy = d.createdBy(); e.createdAt = d.createdAt(); e.updatedBy = d.updatedBy(); e.updatedAt = d.updatedAt(); return e;
    }

    static Finding toDomain(FindingEntity e) {
        return new Finding(PersistenceUuid.toDomain(e.findingId), e.taskId, e.findingType, e.statement,
                JudgmentMode.valueOf(e.judgmentMode), EffectiveClass.valueOf(e.effectiveClass), e.scopeRevisionId,
                FindingStatus.valueOf(e.status), e.createdBy, e.createdAt, e.updatedBy, e.updatedAt);
    }

    static FindingEntity toEntity(Finding d) {
        FindingEntity e = new FindingEntity();
        e.findingId = PersistenceUuid.toStorage(d.findingId()); e.taskId = d.taskId(); e.findingType = d.findingType();
        e.statement = d.statement(); e.judgmentMode = d.judgmentMode().name(); e.effectiveClass = d.effectiveClass().name();
        e.scopeRevisionId = d.scopeRevisionId(); e.status = d.status().name(); e.createdBy = d.createdBy(); e.createdAt = d.createdAt();
        e.updatedBy = d.updatedBy(); e.updatedAt = d.updatedAt(); return e;
    }

    static FindingEvidence toDomain(FindingEvidenceEntity e) {
        return new FindingEvidence(e.findingId, e.snapshotId, e.relationType, e.createdBy, e.createdAt, e.updatedBy, e.updatedAt);
    }

    static FindingEvidenceEntity toEntity(FindingEvidence d) {
        FindingEvidenceEntity e = new FindingEvidenceEntity();
        e.findingId = d.findingId(); e.snapshotId = d.snapshotId(); e.relationType = d.relationType();
        e.createdBy = d.createdBy(); e.createdAt = d.createdAt(); e.updatedBy = d.updatedBy(); e.updatedAt = d.updatedAt(); return e;
    }

    static ComputationWitness toDomain(ComputationWitnessEntity e) {
        return new ComputationWitness(PersistenceUuid.toDomain(e.witnessId), e.findingId, e.algorithmVersion,
                e.ruleSetVersion, e.inputDigest, e.outputDigest, e.snapshotIds, e.computedAt,
                RecomputeStatus.valueOf(e.recomputeStatus), e.recomputedAt, e.createdBy, e.createdAt, e.updatedBy, e.updatedAt);
    }

    static ComputationWitnessEntity toEntity(ComputationWitness d) {
        ComputationWitnessEntity e = new ComputationWitnessEntity();
        e.witnessId = PersistenceUuid.toStorage(d.witnessId()); e.findingId = d.findingId(); e.algorithmVersion = d.algorithmVersion();
        e.ruleSetVersion = d.ruleSetVersion(); e.inputDigest = d.inputDigest(); e.outputDigest = d.outputDigest();
        e.snapshotIds = d.snapshotIds(); e.computedAt = d.computedAt(); e.recomputeStatus = d.recomputeStatus().name();
        e.recomputedAt = d.recomputedAt(); e.createdBy = d.createdBy(); e.createdAt = d.createdAt(); e.updatedBy = d.updatedBy();
        e.updatedAt = d.updatedAt(); return e;
    }

    static ProfessionalConfirmation toDomain(ProfessionalConfirmationEntity e) {
        return new ProfessionalConfirmation(PersistenceUuid.toDomain(e.confirmationId), e.findingId, e.confirmedBy,
                e.confirmedAt, e.scopeRevisionId, ConfirmationDecision.valueOf(e.decision), e.rationale, e.evidenceRefs,
                e.createdBy, e.createdAt, e.updatedBy, e.updatedAt);
    }

    static ProfessionalConfirmationEntity toEntity(ProfessionalConfirmation d) {
        ProfessionalConfirmationEntity e = new ProfessionalConfirmationEntity();
        e.confirmationId = PersistenceUuid.toStorage(d.confirmationId()); e.findingId = d.findingId(); e.confirmedBy = d.confirmedBy();
        e.confirmedAt = d.confirmedAt(); e.scopeRevisionId = d.scopeRevisionId(); e.decision = d.decision().name();
        e.rationale = d.rationale(); e.evidenceRefs = d.evidenceRefs(); e.createdBy = d.createdBy(); e.createdAt = d.createdAt();
        e.updatedBy = d.updatedBy(); e.updatedAt = d.updatedAt(); return e;
    }
}
