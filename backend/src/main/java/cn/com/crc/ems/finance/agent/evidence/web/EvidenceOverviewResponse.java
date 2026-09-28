package cn.com.crc.ems.finance.agent.evidence.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.evidence.domain.ComputationWitness;
import cn.com.crc.ems.finance.agent.evidence.domain.CoverageRecord;
import cn.com.crc.ems.finance.agent.evidence.domain.FactSnapshot;
import cn.com.crc.ems.finance.agent.evidence.domain.Finding;
import cn.com.crc.ems.finance.agent.evidence.domain.ProfessionalConfirmation;

/** 证据、覆盖度和判断页面的脱敏查询响应。 */
public record EvidenceOverviewResponse(UUID taskId, List<SnapshotView> snapshots, List<CoverageView> coverages,
        List<FindingView> findings) {

    /** 组装页面所需的最小脱敏视图。 */
    public static EvidenceOverviewResponse of(UUID taskId, List<FactSnapshot> snapshots,
            List<CoverageRecord> coverages, List<Finding> findings,
            java.util.function.Function<UUID, ComputationWitness> witnessByFinding,
            java.util.function.Function<UUID, ProfessionalConfirmation> confirmationByFinding) {
        return new EvidenceOverviewResponse(taskId, snapshots.stream().map(SnapshotView::from).toList(),
                coverages.stream().map(CoverageView::from).toList(), findings.stream()
                        .map(finding -> FindingView.from(finding, witnessByFinding.apply(finding.findingId()),
                                confirmationByFinding.apply(finding.findingId())))
                        .toList());
    }

    public record SnapshotView(UUID snapshotId, UUID stepId, String sourceObjectType, String sourceObjectKey,
            Instant checkedAt, Instant effectiveFrom, Instant effectiveTo, String effectiveClass, String sourceVersion,
            String resultDigest, String fieldPolicyVersion, String schemaVersion) {
        private static SnapshotView from(FactSnapshot snapshot) {
            return new SnapshotView(snapshot.snapshotId(), snapshot.stepId(), snapshot.sourceObjectType(),
                    snapshot.sourceObjectKey(), snapshot.checkedAt(), snapshot.effectiveFrom(), snapshot.effectiveTo(),
                    snapshot.effectiveClass().name(), snapshot.sourceVersion(), snapshot.resultDigest(),
                    snapshot.fieldPolicyVersion(), snapshot.schemaVersion());
        }
    }

    public record CoverageView(UUID coverageId, UUID stepId, String queryTemplateVersion, Integer pageCount,
            Long expectedTotal, Long observedTotal, String traversedPaths, String uncoveredPaths, String failures,
            String sourceWatermark, String resultSetDigest, String completeness, Instant checkedAt) {
        private static CoverageView from(CoverageRecord coverage) {
            return new CoverageView(coverage.coverageId(), coverage.stepId(), coverage.queryTemplateVersion(),
                    coverage.pageCount(), coverage.expectedTotal(), coverage.observedTotal(), coverage.traversedPaths(),
                    coverage.uncoveredPaths(), coverage.failures(), coverage.sourceWatermark(), coverage.resultSetDigest(),
                    coverage.completeness().name(), coverage.checkedAt());
        }
    }

    public record FindingView(UUID findingId, String findingType, String statement, String judgmentMode,
            String effectiveClass, String status, UUID scopeRevisionId, WitnessView witness,
            ConfirmationView confirmation) {
        private static FindingView from(Finding finding, ComputationWitness witness,
                ProfessionalConfirmation confirmation) {
            return new FindingView(finding.findingId(), finding.findingType(), finding.statement(),
                    finding.judgmentMode().name(), finding.effectiveClass().name(), finding.status().name(),
                    finding.scopeRevisionId(), witness == null ? null : WitnessView.from(witness),
                    confirmation == null ? null : ConfirmationView.from(confirmation));
        }
    }

    public record ConfirmationView(UUID confirmationId, UUID confirmedBy, Instant confirmedAt,
            UUID scopeRevisionId, String decision, String rationale, String evidenceRefs) {
        private static ConfirmationView from(ProfessionalConfirmation confirmation) {
            return new ConfirmationView(confirmation.confirmationId(), confirmation.confirmedBy(),
                    confirmation.confirmedAt(), confirmation.scopeRevisionId(), confirmation.decision().name(),
                    confirmation.rationale(), confirmation.evidenceRefs());
        }
    }

    public record WitnessView(String algorithmVersion, String ruleSetVersion, String inputDigest,
            String outputDigest, String snapshotIds, Instant computedAt, String recomputeStatus) {
        private static WitnessView from(ComputationWitness witness) {
            return new WitnessView(witness.algorithmVersion(), witness.ruleSetVersion(), witness.inputDigest(),
                    witness.outputDigest(), witness.snapshotIds(), witness.computedAt(), witness.recomputeStatus().name());
        }
    }
}
