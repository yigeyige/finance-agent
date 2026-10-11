package cn.com.crc.ems.finance.agent.packet.application;

import java.util.List;
import java.util.UUID;

/**
 *
 * @Description: 发布前重查和原子发布所需的不可变输入。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PacketPublicationCommand(UUID taskId, UUID draftId, UUID publicationId, String contentDigest,
        UUID scopeRevisionId, UUID runtimeBundleId, String canonicalJson, String htmlSnapshot,
        String sourceVersions, String coverageSummary, List<ScopeSnapshot> baseline, List<ScopeSnapshot> current,
        UUID actorUserId) {
}
