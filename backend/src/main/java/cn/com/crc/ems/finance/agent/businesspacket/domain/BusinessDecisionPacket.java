package cn.com.crc.ems.finance.agent.businesspacket.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 业务决策包，canonicalContent 为权威规范 JSON 文本，内容摘要绑定不可篡改。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
public record BusinessDecisionPacket(UUID packetId, UUID taskId, int revision, String canonicalContent,
        String contentDigest, Instant createdAt) {

    public BusinessDecisionPacket {
        if (packetId == null || taskId == null) {
            throw new IllegalArgumentException("packetId and taskId are required");
        }
        if (revision < 1) {
            throw new IllegalArgumentException("revision must be positive");
        }
        if (canonicalContent == null || canonicalContent.isBlank()) {
            throw new IllegalArgumentException("canonicalContent is required");
        }
        if (contentDigest == null || !contentDigest.matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException("contentDigest must be a 64-character hexadecimal digest");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt is required");
        }
    }
}
