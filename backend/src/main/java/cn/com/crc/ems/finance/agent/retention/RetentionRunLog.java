package cn.com.crc.ems.finance.agent.retention;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.crc.ems.finance.agent.shared.domain.JsonCodec;

/**
 * @Description: 将清理结果写入专用 Logback 文件，并从已知日志文件有界读取管理员页面所需记录。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 20:00:00
 */
public final class RetentionRunLog implements Consumer<RetentionJob.Run> {
    public record Entry(String jobCode, String startedAt, String finishedAt, String status, Map<String, Long> deleted,
            long workspaceFiles, long objectFiles, long localLogFiles, List<String> errors) { }

    private static final Logger LOG = LoggerFactory.getLogger("cleanup.run");
    private static final int MAX_FILES = 5;
    private static final int MAX_BYTES_PER_FILE = 1_000_000;
    private static final int MAX_ENTRIES = 50;
    private final Path root;

    RetentionRunLog(Path root) { this.root = root.toAbsolutePath().normalize(); }

    /**
     * @description: 只把固定字段和脱敏错误类型写入清理日志，不记录业务原文。
     * @param: run 一次运行的汇总。
     * @author: yigeyige99
     */
    @Override
    public void accept(RetentionJob.Run run) {
        Entry entry = new Entry(RetentionScheduleService.JOB_CODE, run.startedAt().toString(), run.finishedAt().toString(),
                run.success() ? "成功" : "失败", run.deleted(), run.workspaceFiles(),
                run.objectFiles(), run.localLogFiles(), run.errors());
        try { LOG.info("{}", JsonCodec.mapper().writeValueAsString(entry)); }
        catch (IOException exception) { throw new IllegalStateException("清理日志无法序列化", exception); }
    }

    /**
     * @description: 只读取专用清理日志的末尾有限字节，过滤坏行并返回最近 50 次运行。
     * @return: 按开始时间降序排列的运行记录。
     * @throws: IOException 日志目录或文件读取失败。
     * @author: yigeyige99
     */
    List<Entry> recent() throws IOException {
        if (!Files.exists(root, LinkOption.NOFOLLOW_LINKS)) return List.of();
        if (Files.isSymbolicLink(root) || !Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("清理日志目录不可用");
        }
        List<Path> files;
        try (var stream = Files.list(root)) {
            files = stream.filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                    .filter(path -> cleanupFile(path.getFileName().toString()))
                    .sorted(Comparator.comparing((Path path) -> path.getFileName().toString()).reversed())
                    .limit(MAX_FILES).toList();
        }
        List<Entry> entries = new ArrayList<>();
        for (Path file : files) {
            for (String line : tail(file).split("\\R")) {
                if (line.isBlank()) continue;
                try {
                    Entry entry = JsonCodec.mapper().readValue(line, Entry.class);
                    if (entry.startedAt() != null && entry.status() != null
                            && entry.deleted() != null && entry.errors() != null) entries.add(entry);
                } catch (IOException ignored) { /* 非清理 JSON 行不暴露到页面。 */ }
            }
        }
        return entries.stream().sorted(Comparator.comparing(Entry::startedAt).reversed())
                .limit(MAX_ENTRIES).toList();
    }

    private static boolean cleanupFile(String name) {
        return name.equals("cleanup.log") || name.matches("cleanup\\.\\d{4}-\\d{2}-\\d{2}\\.\\d+\\.log");
    }

    private static String tail(Path file) throws IOException {
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
            long start = Math.max(0, channel.size() - MAX_BYTES_PER_FILE);
            channel.position(start);
            ByteBuffer buffer = ByteBuffer.allocate((int) (channel.size() - start));
            while (buffer.hasRemaining() && channel.read(buffer) > 0) { }
            String value = new String(buffer.array(), 0, buffer.position(), StandardCharsets.UTF_8);
            if (start > 0) {
                int newline = value.indexOf('\n');
                return newline < 0 ? "" : value.substring(newline + 1);
            }
            return value;
        }
    }
}
