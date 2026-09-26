package cn.com.crc.ems.finance.agent.evidence;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.evidence.domain.ComputationWitness;
import cn.com.crc.ems.finance.agent.evidence.domain.CoverageCompleteness;
import cn.com.crc.ems.finance.agent.evidence.domain.CoverageRecord;
import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;
import cn.com.crc.ems.finance.agent.evidence.domain.FactSnapshot;
import cn.com.crc.ems.finance.agent.evidence.domain.Finding;
import cn.com.crc.ems.finance.agent.evidence.domain.FindingStatus;
import cn.com.crc.ems.finance.agent.evidence.domain.JudgmentMode;
import cn.com.crc.ems.finance.agent.evidence.domain.RecomputeStatus;

class EvidenceDomainModelTest {

    private static final UUID TASK_ID = UUID.randomUUID();
    private static final UUID STEP_ID = UUID.randomUUID();
    private static final UUID SCOPE_ID = UUID.randomUUID();
    private static final Instant NOW = Instant.parse("2026-09-26T00:00:00Z");

    @Test
    void factSnapshotRejectsInvalidEffectiveRange() {
        assertThatThrownBy(() -> new FactSnapshot(UUID.randomUUID(), TASK_ID, STEP_ID, UUID.randomUUID(),
                "ROLE", "role-1", "{\"code\":\"A\"}", NOW, NOW.plusSeconds(60), NOW,
                EffectiveClass.CURRENT_EFFECTIVE, "source-v1", "a".repeat(64), "fields-v1", "scope-v1",
                null, NOW, null, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void coverageRecordRejectsNegativeCounts() {
        assertThatThrownBy(() -> new CoverageRecord(UUID.randomUUID(), TASK_ID, STEP_ID, SCOPE_ID,
                "query-v1", "a".repeat(64), "paged", -1, 1L, 1L, "[]", "[]", "[]", null,
                "b".repeat(64), CoverageCompleteness.PARTIAL, NOW, null, NOW, null, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findingPublishabilityRequiresMatchingEvidenceChain() {
        Finding finding = new Finding(UUID.randomUUID(), TASK_ID, "CONFIG_RISK", "A finding",
                JudgmentMode.DETERMINISTIC, EffectiveClass.CURRENT_EFFECTIVE, SCOPE_ID,
                FindingStatus.CONFIRMED, null, NOW, null, NOW);

        assertThatThrownBy(() -> finding.requirePublishable(true, false, false))
                .isInstanceOf(IllegalStateException.class);
        assertThatCode(() -> finding.requirePublishable(true, true, false))
                .doesNotThrowAnyException();
    }

    @Test
    void computationWitnessRequiresVersionedDigestsAndSnapshots() {
        assertThatThrownBy(() -> new ComputationWitness(UUID.randomUUID(), UUID.randomUUID(), "", "rules-v1",
                "a".repeat(64), "b".repeat(64), "[]", NOW, RecomputeStatus.NOT_RUN, null,
                null, NOW, null, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
