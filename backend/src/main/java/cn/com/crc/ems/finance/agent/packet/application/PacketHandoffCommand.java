package cn.com.crc.ems.finance.agent.packet.application;

import java.util.UUID;

/** 已发布决策包交接命令。 */
public record PacketHandoffCommand(UUID taskId, UUID packetRevisionId, String ownerReference,
        String externalProcessReference, String downstreamStatus, UUID actorUserId) {

    public PacketHandoffCommand {
        if (taskId == null || packetRevisionId == null || actorUserId == null
                || ownerReference == null || ownerReference.isBlank()
                || downstreamStatus == null || downstreamStatus.isBlank()) {
            throw new IllegalArgumentException("handoff command is incomplete");
        }
    }
}
