package cn.com.crc.ems.finance.agent.packet.domain;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/**
 *
 * @Description: 决策包草稿，规范 JSON 是权威内容。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PacketDraft(UUID draftId, UUID taskId, int draftNo, UUID scopeRevisionId, UUID runtimeBundleId,
        VersionedJson canonicalContent, String contentDigest, PacketDraftStatus status, UUID createdBy,
        Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public PacketDraft {
        requireId(draftId, "draftId");
        requireId(taskId, "taskId");
        requireId(scopeRevisionId, "scopeRevisionId");
        requireId(runtimeBundleId, "runtimeBundleId");
        if (draftNo < 1) {
            throw new IllegalArgumentException("draftNo must be positive");
        }
        if (canonicalContent == null) {
            throw new IllegalArgumentException("canonicalContent is required");
        }
        requireDigest(contentDigest, "contentDigest");
        if (status == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("status and audit timestamps are required");
        }
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    static void requireDigest(String value, String name) {
        if (value == null || !value.matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException(name + " must be a 64-character hexadecimal digest");
        }
    }
}
