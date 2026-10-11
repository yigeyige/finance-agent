package cn.com.crc.ems.finance.agent.packet.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.application.PacketHandoffResult;

/**
 *
 * @Description: 决策包交接响应。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PacketHandoffResponse(UUID taskId, UUID packetRevisionId, UUID handoffId,
        String ownerReference, String externalProcessReference, String downstreamStatus,
        String taskStatus, Instant handedOffAt) {

    public static PacketHandoffResponse from(PacketHandoffResult result) {
        var handoff = result.handoff();
        return new PacketHandoffResponse(result.task().taskId(), handoff.packetRevisionId(), handoff.handoffId(),
                handoff.ownerReference(), handoff.externalProcessReference(), handoff.downstreamStatus(),
                result.task().status().name(), handoff.handedOffAt());
    }
}
