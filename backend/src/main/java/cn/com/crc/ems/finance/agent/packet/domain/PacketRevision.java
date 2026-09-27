package cn.com.crc.ems.finance.agent.packet.domain;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 已发布的不可变决策包修订，HTML 仅作为 JSON 权威内容的快照。 */
public record PacketRevision(UUID packetRevisionId, UUID taskId, int revisionNo, UUID draftId,
        VersionedJson canonicalContent, String htmlSnapshot, String contentDigest, Instant checkedAt,
        VersionedJson sourceVersions, VersionedJson coverageSummary, UUID publishedBy, Instant publishedAt,
        UUID supersedesRevisionId, UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public PacketRevision {
        requireId(packetRevisionId, "packetRevisionId");
        requireId(taskId, "taskId");
        requireId(draftId, "draftId");
        if (revisionNo < 1) {
            throw new IllegalArgumentException("revisionNo must be positive");
        }
        if (canonicalContent == null || sourceVersions == null || coverageSummary == null) {
            throw new IllegalArgumentException("canonical JSON and publication summaries are required");
        }
        if (htmlSnapshot == null || htmlSnapshot.isBlank()) {
            throw new IllegalArgumentException("htmlSnapshot must not be blank");
        }
        PacketDraft.requireDigest(contentDigest, "contentDigest");
        if (publishedBy == null || checkedAt == null || publishedAt == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("publication and audit timestamps are required");
        }
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }
}
