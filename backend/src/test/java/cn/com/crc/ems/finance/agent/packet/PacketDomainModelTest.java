package cn.com.crc.ems.finance.agent.packet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.packet.application.PacketRevisionRepository;
import cn.com.crc.ems.finance.agent.packet.domain.PacketDraft;
import cn.com.crc.ems.finance.agent.packet.domain.PacketDraftStatus;
import cn.com.crc.ems.finance.agent.packet.domain.PacketReview;
import cn.com.crc.ems.finance.agent.packet.domain.ReviewDecision;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

class PacketDomainModelTest {

    private static final UUID TASK_ID = UUID.randomUUID();
    private static final UUID SCOPE_ID = UUID.randomUUID();
    private static final UUID BUNDLE_ID = UUID.randomUUID();
    private static final UUID REVIEWER_ID = UUID.randomUUID();
    private static final Instant NOW = Instant.parse("2026-09-27T00:00:00Z");
    private static final String DIGEST = "a".repeat(64);

    @Test
    void reviewRequiresExactCurrentDraftBindings() {
        PacketDraft draft = draft(PacketDraftStatus.CURRENT);
        PacketReview review = new PacketReview(UUID.randomUUID(), draft.draftId(), REVIEWER_ID, DIGEST,
                SCOPE_ID, BUNDLE_ID, ReviewDecision.APPROVE, NOW, "ok", REVIEWER_ID, NOW, REVIEWER_ID, NOW);

        assertThatCode(() -> review.requireMatches(draft)).doesNotThrowAnyException();
        PacketReview mismatched = new PacketReview(UUID.randomUUID(), draft.draftId(), REVIEWER_ID,
                "b".repeat(64), SCOPE_ID, BUNDLE_ID, ReviewDecision.APPROVE, NOW, null, REVIEWER_ID, NOW,
                REVIEWER_ID, NOW);
        assertThatThrownBy(() -> mismatched.requireMatches(draft)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reviewRejectsStaleDraftAndRevisionRepositoryHasNoMutationMethods() {
        PacketDraft staleDraft = draft(PacketDraftStatus.STALE);
        PacketReview review = new PacketReview(UUID.randomUUID(), staleDraft.draftId(), REVIEWER_ID, DIGEST,
                SCOPE_ID, BUNDLE_ID, ReviewDecision.APPROVE, NOW, null, REVIEWER_ID, NOW, REVIEWER_ID, NOW);
        assertThatThrownBy(() -> review.requireMatches(staleDraft)).isInstanceOf(IllegalArgumentException.class);

        assertThat(PacketRevisionRepository.class.getMethods())
                .extracting(java.lang.reflect.Method::getName)
                .containsExactlyInAnyOrder("findById", "findPublishedByTaskId", "insert");
    }

    @Test
    void draftRejectsInvalidDigestAndJson() {
        assertThatThrownBy(() -> new PacketDraft(UUID.randomUUID(), TASK_ID, 1, SCOPE_ID, BUNDLE_ID,
                new VersionedJson("packet-v1", "{}"), "short", PacketDraftStatus.CURRENT,
                REVIEWER_ID, NOW, REVIEWER_ID, NOW)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PacketDraft(UUID.randomUUID(), TASK_ID, 1, SCOPE_ID, BUNDLE_ID,
                new VersionedJson("packet-v1", "{} {}"), DIGEST, PacketDraftStatus.CURRENT,
                REVIEWER_ID, NOW, REVIEWER_ID, NOW)).isInstanceOf(IllegalArgumentException.class);
    }

    private static PacketDraft draft(PacketDraftStatus status) {
        return new PacketDraft(UUID.randomUUID(), TASK_ID, 1, SCOPE_ID, BUNDLE_ID,
                new VersionedJson("packet-v1", "{\"findingCount\":1}"), DIGEST, status,
                REVIEWER_ID, NOW, REVIEWER_ID, NOW);
    }
}
