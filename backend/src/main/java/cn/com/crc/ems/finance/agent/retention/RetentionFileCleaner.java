package cn.com.crc.ems.finance.agent.retention;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: 在受控根目录内按文件修改时间清理 Workspace 和临时对象，拒绝符号链接与身份路径越界。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 20:00:00
 */
final class RetentionFileCleaner {
    private RetentionFileCleaner() { }

    /**
     * @description: 仅删除 user/task/session 三层 UUID 工作区内的到期文件，排除嵌套对象根。
     * @param: root 已配置的 Workspace 根。
     * @param: objects 临时对象根。
     * @param: cutoff 到期截止时间。
     * @return: 删除的普通文件数。
     * @throws: IOException 文件遍历或删除失败。
     * @author: yigeyige99
     */
    static long cleanWorkspace(Path root, Path objects, Instant cutoff) throws IOException {
        return clean(root, objects, cutoff, true);
    }

    /**
     * @description: 仅删除临时对象根内的到期普通文件，包含遗留的上传临时文件。
     * @param: root 已配置的临时对象根。
     * @param: cutoff 到期截止时间。
     * @return: 删除的普通文件数。
     * @throws: IOException 文件遍历或删除失败。
     * @author: yigeyige99
     */
    static long cleanObjects(Path root, Instant cutoff) throws IOException {
        return clean(root, null, cutoff, false);
    }

    /**
     * @description: 删除本地启动脚本产生的旧轮转日志，兼容先前按启动时间生成的日志文件名。
     * @param: configuredRoot 本地脚本日志根。
     * @param: cutoff 到期截止时间。
     * @return: 删除的旧日志数。
     * @throws: IOException 目录或文件访问失败。
     * @author: yigeyige99
     */
    static long cleanLocalLogs(Path configuredRoot, Instant cutoff) throws IOException {
        Path root = configuredRoot.toAbsolutePath().normalize();
        if (!Files.exists(root, LinkOption.NOFOLLOW_LINKS)) return 0;
        if (Files.isSymbolicLink(root) || !Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("local log root is not a real directory");
        }
        long deleted = 0;
        try (var paths = Files.list(root)) {
            for (Path file : paths.toList()) {
                String name = file.getFileName().toString();
                if (!name.matches("(backend|frontend)-\\d{8}-\\d{6}(\\.(out|err)|\\.\\d{8}\\.\\d{3})\\.log")) {
                    continue;
                }
                if (Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)
                        && Files.getLastModifiedTime(file, LinkOption.NOFOLLOW_LINKS)
                                .toInstant().isBefore(cutoff)) {
                    Files.delete(file);
                    deleted++;
                }
            }
        }
        return deleted;
    }

    private static long clean(Path configuredRoot, Path excluded, Instant cutoff, boolean workspace)
            throws IOException {
        if (configuredRoot == null || cutoff == null) throw new IllegalArgumentException("root and cutoff required");
        Path root = configuredRoot.toAbsolutePath().normalize();
        if (!Files.exists(root, LinkOption.NOFOLLOW_LINKS)) return 0;
        if (Files.isSymbolicLink(root) || !Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("retention root is not a real directory");
        }
        Path excludedRoot = excluded == null ? null : excluded.toAbsolutePath().normalize();
        AtomicLong count = new AtomicLong();
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) {
                if (excludedRoot != null && directory.equals(excludedRoot)) return FileVisitResult.SKIP_SUBTREE;
                if (workspace && !directory.equals(root) && !workspacePath(root, directory)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relative = root.relativize(file);
                boolean owned = !workspace || relative.getNameCount() >= 4;
                if (owned && attrs.isRegularFile()
                        && attrs.lastModifiedTime().toInstant().isBefore(cutoff)) {
                    Files.delete(file);
                    count.incrementAndGet();
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path directory, IOException error) throws IOException {
                if (error != null) throw error;
                if (!directory.equals(root)) {
                    try { Files.delete(directory); }
                    catch (DirectoryNotEmptyException ignored) { /* 留存未到期文件或子目录。 */ }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return count.get();
    }

    private static boolean workspacePath(Path root, Path directory) {
        Path relative = root.relativize(directory);
        int check = Math.min(3, relative.getNameCount());
        for (int index = 0; index < check; index++) {
            try { UUID.fromString(relative.getName(index).toString()); }
            catch (IllegalArgumentException exception) { return false; }
        }
        return true;
    }
}
