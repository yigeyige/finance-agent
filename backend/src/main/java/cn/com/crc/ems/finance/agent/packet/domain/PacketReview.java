package cn.com.crc.ems.finance.agent.packet.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** 绑定草稿内容、范围和运行版本的决策包复核。 */
public record PacketReview(UUID reviewId, UUID draftId, UUID reviewerUserId, String contentDigest,
        UUID scopeRevisionId, UUID runtimeBundleId, ReviewDecision decision, Instant reviewedAt, String comment,
        UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public PacketReview {
        requireId(reviewId, "reviewId");
        requireId(draftId, "draftId");
        requireId(reviewerUserId, "reviewerUserId");
        requireId(scopeRevisionId, "scopeRevisionId");
        requireId(runtimeBundleId, "runtimeBundleId");
        PacketDraft.requireDigest(contentDigest, "contentDigest");
        if (decision == null || reviewedAt == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("decision and audit timestamps are required");
        }
    }

    /** 校验复核仍然针对当前草稿的准确内容、范围和运行版本。 */
    public void requireMatches(PacketDraft draft) {
        Objects.requireNonNull(draft, "draft");
        if (draft.status() != PacketDraftStatus.CURRENT || !draft.draftId().equals(draftId)
                || !contentDigest.equalsIgnoreCase(draft.contentDigest())
                || !scopeRevisionId.equals(draft.scopeRevisionId())
                || !runtimeBundleId.equals(draft.runtimeBundleId())) {
            throw new IllegalArgumentException("review does not match the current packet draft");
        }
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }
}
