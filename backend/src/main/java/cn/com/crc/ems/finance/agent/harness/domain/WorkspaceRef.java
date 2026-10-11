package cn.com.crc.ems.finance.agent.harness.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 受控 workspace 制品的引用，不保存文件原文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record WorkspaceRef(UUID workspaceRefId, UUID sessionId, UUID taskId, UUID userId,
        String relativePath, String objectRef, String contentDigest, String mediaType, UUID createdBy,
        Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public WorkspaceRef {
        requireId(workspaceRefId, "workspaceRefId");
        requireId(sessionId, "sessionId");
        requireId(taskId, "taskId");
        requireId(userId, "userId");
        requireText(relativePath, "relativePath");
        requireText(objectRef, "objectRef");
        if (relativePath.startsWith("/") || relativePath.startsWith("\\")
                || relativePath.contains("../") || relativePath.contains("..\\")
                || relativePath.equals("..")) {
            throw new IllegalArgumentException("relativePath must stay inside the workspace");
        }
        if (contentDigest == null || !contentDigest.matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException("contentDigest must be a 64-character hexadecimal digest");
        }
        if (createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("audit timestamps are required");
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
