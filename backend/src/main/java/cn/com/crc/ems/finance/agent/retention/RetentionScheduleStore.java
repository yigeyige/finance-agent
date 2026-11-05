package cn.com.crc.ems.finance.agent.retention;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;

import cn.com.crc.ems.finance.agent.shared.domain.JsonCodec;

/**
 * @Description: 在受控配置目录内持久保存单体清理任务的频率和执行时间，不受业务数据清理影响。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 20:00:00
 */
final class RetentionScheduleStore {
    static final Schedule DEFAULT = new Schedule(7, "02:00");
    private static final String FILE_NAME = "retention-cleanup.json";
    private final Path root;

    RetentionScheduleStore(Path root) { this.root = root.toAbsolutePath().normalize(); }

    /**
     * @description: 读取持久配置；首次启动时使用周日 02:00，兼容旧版每周配置。
     * @return: 有效的调度频率和执行时间。
     * @throws: IOException 文件不可读取或配置损坏。
     * @author: yigeyige99
     */
    Schedule read() throws IOException {
        Path file = file();
        if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) return DEFAULT;
        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS) || Files.size(file) > 1024) {
            throw new IOException("调度配置文件不可用");
        }
        try {
            Schedule schedule = JsonCodec.mapper().readValue(Files.readString(file), Schedule.class);
            if (schedule == null) throw new IllegalArgumentException("空调度配置");
            return schedule;
        } catch (Exception exception) {
            throw new IOException("调度配置内容无效", exception);
        }
    }

    /**
     * @description: 在同一目录原子替换配置文件，避免进程重启读到半写内容。
     * @param: schedule 已校验的调度频率和执行时间。
     * @throws: IOException 配置目录或文件不可写。
     * @author: yigeyige99
     */
    void write(Schedule schedule) throws IOException {
        if (schedule == null) throw new IllegalArgumentException("调度时间不能为空");
        file();
        Files.createDirectories(root);
        Path temporary = Files.createTempFile(root, ".retention-cleanup-", ".tmp");
        try {
            Files.writeString(temporary, JsonCodec.mapper().writeValueAsString(schedule));
            Files.move(temporary, root.resolve(FILE_NAME), StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private Path file() throws IOException {
        if (Files.exists(root, LinkOption.NOFOLLOW_LINKS)
                && (Files.isSymbolicLink(root) || !Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS))) {
            throw new IOException("调度配置目录不可用");
        }
        Path file = root.resolve(FILE_NAME);
        if (Files.isSymbolicLink(file)) throw new IOException("调度配置文件不可用");
        return file;
    }

    enum Frequency { DAILY, WEEKLY, MONTHLY }

    record Schedule(Frequency frequency, Integer dayOfWeek, Integer dayOfMonth, String time) {
        Schedule {
            if (frequency == null) frequency = Frequency.WEEKLY;
            if (time == null || !time.matches("(?:[01]\\d|2[0-3]):[0-5]\\d")) {
                throw new IllegalArgumentException("调度时间无效");
            }
            LocalTime.parse(time);
            switch (frequency) {
                case DAILY -> { dayOfWeek = null; dayOfMonth = null; }
                case WEEKLY -> {
                    if (dayOfWeek == null || dayOfWeek < 1 || dayOfWeek > 7) {
                        throw new IllegalArgumentException("每周执行日无效");
                    }
                    dayOfMonth = null;
                }
                case MONTHLY -> {
                    if (dayOfMonth == null || dayOfMonth < 1 || dayOfMonth > 28) {
                        throw new IllegalArgumentException("每月执行日无效");
                    }
                    dayOfWeek = null;
                }
            }
        }

        Schedule(int dayOfWeek, String time) { this(Frequency.WEEKLY, dayOfWeek, null, time); }
    }
}
