package cn.com.crc.ems.finance.agent.packet;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;
import cn.com.crc.ems.finance.agent.packet.application.RecheckDecision;
import cn.com.crc.ems.finance.agent.packet.application.ScopeChangeType;
import cn.com.crc.ems.finance.agent.packet.application.ScopeRecheckService;
import cn.com.crc.ems.finance.agent.packet.application.ScopeSnapshot;
import cn.com.crc.ems.finance.agent.packet.domain.PacketDraftStatus;
import cn.com.crc.ems.finance.agent.packet.infrastructure.InMemoryScopeRecheckRepository;

class ScopeRecheckServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-28T00:00:00Z");

    @Test
    void unchangedFullScopeIsReadyToPublishAndPersisted() {
        UUID taskId = UUID.randomUUID();
        var repository = new InMemoryScopeRecheckRepository();
        var service = new ScopeRecheckService(repository);
        var baseline = List.of(snapshot("EMP-1", "fields-1", EffectiveClass.CURRENT_EFFECTIVE, "source-1", "rules-1", "template-1"));
        var result = service.recheck(taskId, UUID.randomUUID(), baseline, List.copyOf(baseline), NOW, UUID.randomUUID());
        assertThat(result.decision()).isEqualTo(RecheckDecision.READY_TO_PUBLISH);
        assertThat(result.draftStatus()).isEqualTo(PacketDraftStatus.CURRENT);
        assertThat(repository.findLatestByTaskId(taskId)).isPresent();
    }

    @Test
    void addedObjectAndEveryRelevantChangeStaleDraft() {
        UUID taskId = UUID.randomUUID();
        var service = new ScopeRecheckService(new InMemoryScopeRecheckRepository());
        var baseline = List.of(snapshot("EMP-1", "fields-1", EffectiveClass.CURRENT_EFFECTIVE, "source-1", "rules-1", "template-1"));
        var current = List.of(snapshot("EMP-1", "fields-2", EffectiveClass.FUTURE_SCHEDULED, "source-2", "rules-2", "template-2"),
                snapshot("EMP-2", "fields-3", EffectiveClass.CURRENT_EFFECTIVE, "source-1", "rules-1", "template-1"));
        var result = service.recheck(taskId, UUID.randomUUID(), baseline, current, NOW, UUID.randomUUID());
        assertThat(result.decision()).isEqualTo(RecheckDecision.STALE_DRAFT);
        assertThat(result.draftStatus()).isEqualTo(PacketDraftStatus.STALE);
        assertThat(result.diffs()).hasSize(2);
        assertThat(result.diffs().get(0).changes()).containsExactlyInAnyOrder(ScopeChangeType.FIELDS_CHANGED,
                ScopeChangeType.EFFECTIVE_CLASS_CHANGED, ScopeChangeType.SOURCE_VERSION_CHANGED,
                ScopeChangeType.RULE_VERSION_CHANGED, ScopeChangeType.TEMPLATE_VERSION_CHANGED);
        assertThat(result.diffs().get(1).changes()).containsExactly(ScopeChangeType.ADDED);
    }

    @Test
    void removedObjectIsDetectedEvenWhenSourceVersionLooksUnchanged() {
        var service = new ScopeRecheckService(new InMemoryScopeRecheckRepository());
        var result = service.recheck(UUID.randomUUID(), UUID.randomUUID(),
                List.of(snapshot("EMP-1", "fields-1", EffectiveClass.CURRENT_EFFECTIVE, "same", "rules-1", "template-1")),
                List.of(), NOW, null);
        assertThat(result.diffs().get(0).changes()).containsExactly(ScopeChangeType.REMOVED);
        assertThat(result.decision()).isEqualTo(RecheckDecision.STALE_DRAFT);
    }

    private static ScopeSnapshot snapshot(String key, String fields, EffectiveClass effectiveClass,
            String source, String rules, String template) {
        return new ScopeSnapshot(key, fields, effectiveClass, source, rules, template);
    }
}
