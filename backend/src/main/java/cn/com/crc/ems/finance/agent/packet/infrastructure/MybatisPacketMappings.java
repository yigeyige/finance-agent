package cn.com.crc.ems.finance.agent.packet.infrastructure;

import cn.com.crc.ems.finance.agent.packet.domain.Handoff;
import cn.com.crc.ems.finance.agent.packet.domain.PacketDraft;
import cn.com.crc.ems.finance.agent.packet.domain.PacketDraftStatus;
import cn.com.crc.ems.finance.agent.packet.domain.PacketReview;
import cn.com.crc.ems.finance.agent.packet.domain.PacketRevision;
import cn.com.crc.ems.finance.agent.packet.domain.ReviewDecision;
import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.HandoffEntity;
import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.PacketDraftEntity;
import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.PacketReviewEntity;
import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.PacketRevisionEntity;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 决策包领域对象与 MyBatis-Plus Entity 的显式映射。 */
public final class MybatisPacketMappings {

    private MybatisPacketMappings() {
    }

    /** 将草稿 Entity 映射为领域对象。 */
    public static PacketDraft toDomain(PacketDraftEntity entity) {
        return new PacketDraft(PersistenceUuid.toDomain(entity.draftId), entity.taskId, entity.draftNo,
                entity.scopeRevisionId, entity.runtimeBundleId, json(entity.canonicalContent, "packet-v1"),
                entity.contentDigest, PacketDraftStatus.valueOf(entity.status), entity.createdBy, entity.createdAt,
                entity.updatedBy, entity.updatedAt);
    }

    /** 将领域草稿映射为持久化 Entity。 */
    public static PacketDraftEntity toEntity(PacketDraft draft) {
        PacketDraftEntity entity = new PacketDraftEntity();
        entity.draftId = PersistenceUuid.toStorage(draft.draftId());
        entity.taskId = draft.taskId();
        entity.draftNo = draft.draftNo();
        entity.scopeRevisionId = draft.scopeRevisionId();
        entity.runtimeBundleId = draft.runtimeBundleId();
        entity.canonicalContent = draft.canonicalContent().json();
        entity.contentDigest = draft.contentDigest();
        entity.status = draft.status().name();
        entity.createdBy = draft.createdBy();
        entity.createdAt = draft.createdAt();
        entity.updatedBy = draft.updatedBy();
        entity.updatedAt = draft.updatedAt();
        return entity;
    }

    /** 将复核 Entity 映射为领域对象。 */
    public static PacketReview toDomain(PacketReviewEntity entity) {
        return new PacketReview(PersistenceUuid.toDomain(entity.reviewId), entity.draftId, entity.reviewerUserId,
                entity.contentDigest, entity.scopeRevisionId, entity.runtimeBundleId,
                ReviewDecision.valueOf(entity.decision), entity.reviewedAt, entity.comment, entity.createdBy,
                entity.createdAt, entity.updatedBy, entity.updatedAt);
    }

    /** 将领域复核映射为持久化 Entity。 */
    public static PacketReviewEntity toEntity(PacketReview review) {
        PacketReviewEntity entity = new PacketReviewEntity();
        entity.reviewId = PersistenceUuid.toStorage(review.reviewId());
        entity.draftId = review.draftId();
        entity.reviewerUserId = review.reviewerUserId();
        entity.contentDigest = review.contentDigest();
        entity.scopeRevisionId = review.scopeRevisionId();
        entity.runtimeBundleId = review.runtimeBundleId();
        entity.decision = review.decision().name();
        entity.reviewedAt = review.reviewedAt();
        entity.comment = review.comment();
        entity.createdBy = review.createdBy();
        entity.createdAt = review.createdAt();
        entity.updatedBy = review.updatedBy();
        entity.updatedAt = review.updatedAt();
        return entity;
    }

    /** 将发布修订 Entity 映射为领域对象。 */
    public static PacketRevision toDomain(PacketRevisionEntity entity) {
        return new PacketRevision(PersistenceUuid.toDomain(entity.packetRevisionId), entity.taskId, entity.revisionNo,
                entity.draftId, json(entity.canonicalContent, "packet-v1"), entity.htmlSnapshot, entity.contentDigest,
                entity.checkedAt, json(entity.sourceVersions, "runtime-v1"), json(entity.coverageSummary, "coverage-v1"),
                entity.publishedBy, entity.publishedAt, entity.supersedesRevisionId, entity.createdBy, entity.createdAt,
                entity.updatedBy, entity.updatedAt);
    }

    /** 将领域发布修订映射为持久化 Entity。 */
    public static PacketRevisionEntity toEntity(PacketRevision revision) {
        PacketRevisionEntity entity = new PacketRevisionEntity();
        entity.packetRevisionId = PersistenceUuid.toStorage(revision.packetRevisionId());
        entity.taskId = revision.taskId();
        entity.revisionNo = revision.revisionNo();
        entity.draftId = revision.draftId();
        entity.canonicalContent = revision.canonicalContent().json();
        entity.htmlSnapshot = revision.htmlSnapshot();
        entity.contentDigest = revision.contentDigest();
        entity.checkedAt = revision.checkedAt();
        entity.sourceVersions = revision.sourceVersions().json();
        entity.coverageSummary = revision.coverageSummary().json();
        entity.publishedBy = revision.publishedBy();
        entity.publishedAt = revision.publishedAt();
        entity.supersedesRevisionId = revision.supersedesRevisionId();
        entity.createdBy = revision.createdBy();
        entity.createdAt = revision.createdAt();
        entity.updatedBy = revision.updatedBy();
        entity.updatedAt = revision.updatedAt();
        return entity;
    }

    /** 将交接 Entity 映射为领域对象。 */
    public static Handoff toDomain(HandoffEntity entity) {
        return new Handoff(PersistenceUuid.toDomain(entity.handoffId), entity.packetRevisionId, entity.ownerReference,
                entity.externalProcessReference, entity.downstreamStatus, entity.handedOffBy, entity.handedOffAt,
                entity.createdBy, entity.createdAt, entity.updatedBy, entity.updatedAt);
    }

    /** 将领域交接映射为持久化 Entity。 */
    public static HandoffEntity toEntity(Handoff handoff) {
        HandoffEntity entity = new HandoffEntity();
        entity.handoffId = PersistenceUuid.toStorage(handoff.handoffId());
        entity.packetRevisionId = handoff.packetRevisionId();
        entity.ownerReference = handoff.ownerReference();
        entity.externalProcessReference = handoff.externalProcessReference();
        entity.downstreamStatus = handoff.downstreamStatus();
        entity.handedOffBy = handoff.handedOffBy();
        entity.handedOffAt = handoff.handedOffAt();
        entity.createdBy = handoff.createdBy();
        entity.createdAt = handoff.createdAt();
        entity.updatedBy = handoff.updatedBy();
        entity.updatedAt = handoff.updatedAt();
        return entity;
    }

    private static VersionedJson json(String value, String schemaVersion) {
        return new VersionedJson(schemaVersion, value);
    }
}
