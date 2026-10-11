package cn.com.crc.ems.finance.agent.packet.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.application.PacketPublishedView;
import cn.com.crc.ems.finance.agent.packet.domain.Handoff;

/**
 *
 * @Description: 已发布决策包只读响应，不包含草稿状态或发布前输入。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PublishedPacketResponse(UUID taskId, UUID packetRevisionId, int revisionNo, String canonicalJson,
        String htmlSnapshot, String contentDigest, Instant checkedAt, String sourceVersions,
        String coverageSummary, String limitations, UUID publishedBy, Instant publishedAt,
        String taskStatus, Instant closedAt, HandoffResponse handoff) {

    public static PublishedPacketResponse from(PacketPublishedView view) {
        Handoff handoff = view.handoff();
        return new PublishedPacketResponse(view.taskId(), view.revision().packetRevisionId(), view.revision().revisionNo(),
                view.revision().canonicalContent().json(), view.revision().htmlSnapshot(), view.revision().contentDigest(),
                view.revision().checkedAt(), view.revision().sourceVersions().json(),
                view.revision().coverageSummary().json(), view.limitations(), view.revision().publishedBy(),
                view.revision().publishedAt(), view.taskStatus().name(), view.closedAt(),
                handoff == null ? null : HandoffResponse.from(handoff));
    }

    /** 已发布修订的交接摘要。 */
    public record HandoffResponse(UUID handoffId, UUID packetRevisionId, String ownerReference,
            String externalProcessReference, String downstreamStatus, UUID handedOffBy, Instant handedOffAt) {
        static HandoffResponse from(Handoff handoff) {
            return new HandoffResponse(handoff.handoffId(), handoff.packetRevisionId(), handoff.ownerReference(),
                    handoff.externalProcessReference(), handoff.downstreamStatus(), handoff.handedOffBy(),
                    handoff.handedOffAt());
        }
    }
}
