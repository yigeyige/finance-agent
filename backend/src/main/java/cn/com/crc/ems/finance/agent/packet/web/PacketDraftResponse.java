package cn.com.crc.ems.finance.agent.packet.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.application.PacketDraftAssemblyResult;

/** 决策包草稿响应，JSON 为权威内容，HTML 仅为同摘要快照。 */
public record PacketDraftResponse(UUID taskId, UUID draftId, int draftNo, UUID scopeRevisionId,
        UUID runtimeBundleId, String status, String canonicalJson, String contentDigest, String htmlSnapshot,
        Instant createdAt) {

    public static PacketDraftResponse from(PacketDraftAssemblyResult result) {
        var draft = result.draft();
        return new PacketDraftResponse(draft.taskId(), draft.draftId(), draft.draftNo(), draft.scopeRevisionId(),
                draft.runtimeBundleId(), draft.status().name(), draft.canonicalContent().json(), draft.contentDigest(),
                result.htmlSnapshot(), draft.createdAt());
    }
}
