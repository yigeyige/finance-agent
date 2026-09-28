package cn.com.crc.ems.finance.agent.packet.application;

import java.util.List;
import java.util.UUID;

/** 发布前重查和原子发布所需的不可变输入。 */
public record PacketPublicationCommand(UUID taskId, UUID draftId, UUID publicationId, String contentDigest,
        UUID scopeRevisionId, UUID runtimeBundleId, String canonicalJson, String htmlSnapshot,
        String sourceVersions, String coverageSummary, List<ScopeSnapshot> baseline, List<ScopeSnapshot> current,
        UUID actorUserId) {
}
