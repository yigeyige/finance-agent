package cn.com.crc.ems.finance.agent.harness.application;

import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @Description: 受控 Workspace 中临时制品的元数据，不代表权威证据副本。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:00:00
 */
public record WorkspaceArtifact(UUID userId, UUID taskId, UUID sessionId, String relativePath, Path path,
        String contentDigest, long size, String mediaType) {

    public WorkspaceArtifact {
        Objects.requireNonNull(userId, "userId is required");
        Objects.requireNonNull(taskId, "taskId is required");
        Objects.requireNonNull(sessionId, "sessionId is required");
        Objects.requireNonNull(path, "path is required");
        if (relativePath == null || relativePath.isBlank() || Path.of(relativePath).isAbsolute()
                || Path.of(relativePath).normalize().startsWith("..")) {
            throw new IllegalArgumentException("relativePath must stay inside the workspace");
        }
        if (contentDigest == null || !contentDigest.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("contentDigest must be a SHA-256 hex digest");
        }
        if (size < 0) {
            throw new IllegalArgumentException("size must not be negative");
        }
        if (mediaType == null || mediaType.isBlank()) {
            throw new IllegalArgumentException("mediaType is required");
        }
        path = path.toAbsolutePath().normalize();
    }
}
