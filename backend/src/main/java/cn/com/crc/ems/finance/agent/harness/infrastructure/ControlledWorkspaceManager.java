package cn.com.crc.ems.finance.agent.harness.infrastructure;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;
import cn.com.crc.ems.finance.agent.harness.application.ControlledWorkspaceException;
import cn.com.crc.ems.finance.agent.harness.application.WorkspaceArtifact;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.springframework.util.StringUtils;

/**
 *
 * @Description: 按用户、任务和 session 隔离临时 Workspace，并集中执行路径、类型和配额校验。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:00:00
 */
public class ControlledWorkspaceManager {

    private final Path root;
    private final long maxBytes;
    private final long maxFileBytes;
    private final Set<String> allowedMediaTypes;
    private final ConcurrentMap<Path, Object> locks = new ConcurrentHashMap<>();

    public ControlledWorkspaceManager(FinanceAgentProperties.Workspace settings) {
        this(Path.of(Objects.requireNonNull(settings, "workspace settings are required").getRoot()), settings.getMaxBytes(),
                settings.getMaxFileBytes(), settings.getAllowedMediaTypes());
    }

    public ControlledWorkspaceManager(Path root, long maxBytes, long maxFileBytes, Set<String> allowedMediaTypes) {
        if (root == null || root.toString().isBlank()) {
            throw new IllegalArgumentException("workspace root is required");
        }
        if (maxBytes <= 0 || maxFileBytes <= 0 || maxFileBytes > maxBytes) {
            throw new IllegalArgumentException("workspace quotas are invalid");
        }
        if (allowedMediaTypes == null || allowedMediaTypes.isEmpty()) {
            throw new IllegalArgumentException("workspace media types are required");
        }
        this.root = root.toAbsolutePath().normalize();
        this.maxBytes = maxBytes;
        this.maxFileBytes = maxFileBytes;
        this.allowedMediaTypes = Collections.unmodifiableSet(allowedMediaTypes.stream().filter(StringUtils::hasText)
                .map(value -> value.trim().toLowerCase(Locale.ROOT)).collect(java.util.stream.Collectors.toSet()));
        if (this.allowedMediaTypes.isEmpty()) {
            throw new IllegalArgumentException("workspace media types are required");
        }
    }

    /**
     *
     * @description: 返回服务端根据三类业务身份生成的 Workspace 路径。
     *
     * @param: userId 用户标识。
     * @param: taskId 任务标识。
     * @param: sessionId session 标识。
     * @return: 隔离的 Workspace 路径。
     */
    public Path workspacePath(UUID userId, UUID taskId, UUID sessionId) {
        requireIds(userId, taskId, sessionId);
        return root.resolve(userId.toString()).resolve(taskId.toString()).resolve(sessionId.toString()).normalize();
    }

    /**
     *
     * @description: 在指定 Workspace 中解析并校验相对路径，不接受调用方覆盖根目录。
     *
     * @param: userId 用户标识。
     * @param: taskId 任务标识。
     * @param: sessionId session 标识。
     * @param: relativePath Workspace 内相对路径。
     * @return: 受控目标路径。
     * @throws: ControlledWorkspaceException 路径越界、符号链接或目标不安全时抛出。
     */
    public Path resolve(UUID userId, UUID taskId, UUID sessionId, String relativePath) {
        Path relative = validateRelativePath(relativePath);
        Path workspace = prepareWorkspace(userId, taskId, sessionId);
        Path target = workspace.resolve(relative).normalize();
        ensureWithin(target, workspace, "workspace path escapes its boundary");
        ensureNoSymbolicLinks(workspace, target);
        return target;
    }

    /**
     *
     * @description: 以临时文件和摘要写入受控 Workspace 制品，并在移动前复核配额。
     *
     * @param: userId 用户标识。
     * @param: taskId 任务标识。
     * @param: sessionId session 标识。
     * @param: relativePath Workspace 内相对路径。
     * @param: content 制品内容流。
     * @param: mediaType 媒体类型。
     * @return: 带 SHA-256 摘要和大小的临时制品。
     * @throws: ControlledWorkspaceException 输入、路径、类型或配额不满足时抛出。
     */
    public WorkspaceArtifact write(UUID userId, UUID taskId, UUID sessionId, String relativePath,
            InputStream content, String mediaType) {
        Objects.requireNonNull(content, "content is required");
        String normalizedMediaType = normalizeMediaType(mediaType);
        Path workspace = prepareWorkspace(userId, taskId, sessionId);
        Path target = resolve(userId, taskId, sessionId, relativePath);
        Object lock = locks.computeIfAbsent(workspace, ignored -> new Object());
        synchronized (lock) {
            Path temporary = null;
            try {
                Files.createDirectories(target.getParent());
                ensureNoSymbolicLinks(workspace, target);
                if (Files.exists(target, LinkOption.NOFOLLOW_LINKS) && !Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS)) {
                    throw new ControlledWorkspaceException("workspace target is not a regular file");
                }
                temporary = Files.createTempFile(workspace, ".upload-", ".tmp");
                MessageDigest digest = sha256();
                AtomicLong size = new AtomicLong();
                try (OutputStream output = Files.newOutputStream(temporary, StandardOpenOption.TRUNCATE_EXISTING)) {
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = content.read(buffer)) != -1) {
                        long next = size.addAndGet(read);
                        if (next > maxFileBytes) {
                            throw new ControlledWorkspaceException("workspace file quota exceeded");
                        }
                        digest.update(buffer, 0, read);
                        output.write(buffer, 0, read);
                    }
                }
                long existingSize = Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS) ? Files.size(target) : 0;
                long usage = usageBytes(workspace);
                if (usage - existingSize + size.get() > maxBytes) {
                    throw new ControlledWorkspaceException("workspace quota exceeded");
                }
                ensureNoSymbolicLinks(workspace, target);
                try {
                    Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException atomicFailure) {
                    Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
                }
                temporary = null;
                return new WorkspaceArtifact(userId, taskId, sessionId, normalizeRelativePath(relativePath), target,
                        hex(digest.digest()), size.get(), normalizedMediaType);
            } catch (ControlledWorkspaceException exception) {
                throw exception;
            } catch (IOException exception) {
                throw new ControlledWorkspaceException("workspace operation failed", exception);
            } finally {
                if (temporary != null) {
                    try {
                        Files.deleteIfExists(temporary);
                    } catch (IOException ignored) {
                        // 临时文件清理失败不覆盖原始业务异常。
                    }
                }
            }
        }
    }

    /**
     *
     * @description: 打开指定 Workspace 内的临时制品。
     *
     * @param: userId 用户标识。
     * @param: taskId 任务标识。
     * @param: sessionId session 标识。
     * @param: relativePath Workspace 内相对路径。
     * @return: 只读输入流。
     * @throws: ControlledWorkspaceException 目标不存在或不是普通文件时抛出。
     */
    public InputStream open(UUID userId, UUID taskId, UUID sessionId, String relativePath) {
        Path workspace = prepareWorkspace(userId, taskId, sessionId);
        Path target = resolve(userId, taskId, sessionId, relativePath);
        try {
            if (!Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS)) {
                throw new ControlledWorkspaceException("workspace artifact not found");
            }
            ensureNoSymbolicLinks(workspace, target);
            return Files.newInputStream(target, StandardOpenOption.READ);
        } catch (ControlledWorkspaceException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new ControlledWorkspaceException("workspace artifact cannot be opened", exception);
        }
    }

    /**
     *
     * @description: 删除指定完整 user/task/session 边界内的临时 Workspace。
     *
     * @param: userId 用户标识。
     * @param: taskId 任务标识。
     * @param: sessionId session 标识。
     * @throws: ControlledWorkspaceException 目标边界不安全时抛出。
     */
    public void cleanup(UUID userId, UUID taskId, UUID sessionId) {
        Path workspace = workspacePath(userId, taskId, sessionId);
        ensureWithin(workspace, root, "workspace path escapes root");
        if (workspace.getNameCount() != root.getNameCount() + 3) {
            throw new ControlledWorkspaceException("cleanup requires a complete workspace boundary");
        }
        try {
            if (!Files.exists(workspace, LinkOption.NOFOLLOW_LINKS)) {
                return;
            }
            Files.walkFileTree(workspace, Set.of(), Integer.MAX_VALUE, new java.nio.file.SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path directory, IOException exception) throws IOException {
                    if (exception != null) {
                        throw exception;
                    }
                    Files.deleteIfExists(directory);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException exception) {
            throw new ControlledWorkspaceException("workspace cleanup failed", exception);
        } finally {
            locks.remove(workspace);
        }
    }

    /**
     *
     * @description: 统计指定 Workspace 当前普通文件占用的字节数。
     *
     * @param: userId 用户标识。
     * @param: taskId 任务标识。
     * @param: sessionId session 标识。
     * @return: 当前占用字节数。
     */
    public long usage(UUID userId, UUID taskId, UUID sessionId) {
        return usageBytes(prepareWorkspace(userId, taskId, sessionId));
    }

    private Path prepareWorkspace(UUID userId, UUID taskId, UUID sessionId) {
        Path workspace = workspacePath(userId, taskId, sessionId);
        ensureWithin(workspace, root, "workspace path escapes root");
        try {
            Files.createDirectories(workspace);
            Path rootReal = Files.exists(root, LinkOption.NOFOLLOW_LINKS) ? root.toRealPath() : root;
            Path workspaceReal = workspace.toRealPath();
            ensureWithin(workspaceReal, rootReal, "workspace real path escapes root");
            ensureNoSymbolicLinks(rootReal, workspaceReal);
            return workspaceReal;
        } catch (IOException exception) {
            throw new ControlledWorkspaceException("workspace cannot be prepared", exception);
        }
    }

    private Path validateRelativePath(String value) {
        if (!StringUtils.hasText(value)) {
            throw new ControlledWorkspaceException("workspace relative path is required");
        }
        try {
            Path relative = Paths.get(value);
            if (relative.isAbsolute() || containsParentSegment(relative)) {
                throw new ControlledWorkspaceException("workspace path must be relative and cannot contain ..");
            }
            Path normalized = relative.normalize();
            if (normalized.getNameCount() == 0 || normalized.toString().isBlank()) {
                throw new ControlledWorkspaceException("workspace relative path is required");
            }
            return normalized;
        } catch (ControlledWorkspaceException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ControlledWorkspaceException("workspace path is invalid", exception);
        }
    }

    private String normalizeRelativePath(String value) {
        return validateRelativePath(value).toString().replace('\\', '/');
    }

    private String normalizeMediaType(String value) {
        if (!StringUtils.hasText(value)) {
            throw new ControlledWorkspaceException("workspace media type is required");
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (!allowedMediaTypes.contains(normalized)) {
            throw new ControlledWorkspaceException("workspace media type is not allowed");
        }
        return normalized;
    }

    private long usageBytes(Path workspace) {
        if (!Files.exists(workspace, LinkOption.NOFOLLOW_LINKS)) {
            return 0;
        }
        try (Stream<Path> paths = Files.walk(workspace)) {
            AtomicLong total = new AtomicLong();
            paths.forEach(path -> {
                if (Files.isSymbolicLink(path)) {
                    throw new ControlledWorkspaceException("symbolic links are not allowed in workspace");
                }
                if (path.getFileName() != null && path.getFileName().toString().startsWith(".upload-")) {
                    return;
                }
                if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
                    try {
                        total.addAndGet(Files.size(path));
                    } catch (IOException exception) {
                        throw new ControlledWorkspaceException("workspace usage cannot be calculated", exception);
                    }
                }
            });
            return total.get();
        } catch (ControlledWorkspaceException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new ControlledWorkspaceException("workspace usage cannot be calculated", exception);
        }
    }

    private void ensureNoSymbolicLinks(Path base, Path target) {
        Path normalizedBase = base.toAbsolutePath().normalize();
        Path normalizedTarget = target.toAbsolutePath().normalize();
        ensureWithin(normalizedTarget, normalizedBase, "workspace path escapes its boundary");
        Path current = normalizedBase;
        for (Path segment : normalizedBase.relativize(normalizedTarget)) {
            current = current.resolve(segment);
            if (Files.isSymbolicLink(current)) {
                throw new ControlledWorkspaceException("symbolic links are not allowed in workspace");
            }
        }
    }

    private static void ensureWithin(Path candidate, Path boundary, String message) {
        if (!candidate.toAbsolutePath().normalize().startsWith(boundary.toAbsolutePath().normalize())) {
            throw new ControlledWorkspaceException(message);
        }
    }

    private static boolean containsParentSegment(Path path) {
        for (Path segment : path) {
            if ("..".equals(segment.toString())) {
                return true;
            }
        }
        return false;
    }

    private static void requireIds(UUID userId, UUID taskId, UUID sessionId) {
        Objects.requireNonNull(userId, "userId is required");
        Objects.requireNonNull(taskId, "taskId is required");
        Objects.requireNonNull(sessionId, "sessionId is required");
    }

    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            result.append(String.format(Locale.ROOT, "%02x", value));
        }
        return result.toString();
    }
}
