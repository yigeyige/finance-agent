package cn.com.crc.ems.finance.agent.packet.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.application.PacketPublicationResult;
import cn.com.crc.ems.finance.agent.packet.application.ScopeRecheckDiff;

/**
 *
 * @Description: 发布结果，重查发现变化时返回阻断状态而不返回 revision。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PacketPublicationResponse(UUID taskId, boolean published, String taskStatus,
        UUID packetRevisionId, Integer revisionNo, String contentDigest, String recheckDecision,
        String recheckDigest, Instant checkedAt, List<ScopeRecheckDiff> diffs) {

    public static PacketPublicationResponse from(UUID taskId, PacketPublicationResult result) {
        var recheck = result.recheck();
        var revision = result.revision();
        return new PacketPublicationResponse(taskId, result.published(), result.taskStatus().name(),
                revision == null ? null : revision.packetRevisionId(), revision == null ? null : revision.revisionNo(),
                revision == null ? null : revision.contentDigest(), recheck == null ? null : recheck.decision().name(),
                recheck == null ? null : recheck.resultDigest(), recheck == null ? null : recheck.checkedAt(),
                recheck == null ? List.of() : recheck.diffs());
    }
}
