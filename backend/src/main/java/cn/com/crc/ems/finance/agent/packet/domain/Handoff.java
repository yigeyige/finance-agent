package cn.com.crc.ems.finance.agent.packet.domain;

import java.time.Instant;
import java.util.UUID;

/** 已发布决策包向责任方或外部流程的交接记录。 */
public record Handoff(UUID handoffId, UUID packetRevisionId, String ownerReference,
        String externalProcessReference, String downstreamStatus, UUID handedOffBy, Instant handedOffAt,
        UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public Handoff {
        requireId(handoffId, "handoffId");
        requireId(packetRevisionId, "packetRevisionId");
        requireText(ownerReference, "ownerReference");
        requireText(downstreamStatus, "downstreamStatus");
        requireId(handedOffBy, "handedOffBy");
        if (handedOffAt == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("handoff and audit timestamps are required");
        }
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
