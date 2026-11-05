package cn.com.crc.ems.finance.agent.retention;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


/**
 * @Description: 每周以单一截止时间串行业务表与受控文件清理，并把结果写入专用日志。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 20:00:00
 */
final class RetentionJob {
    record Run(Instant startedAt, Instant finishedAt, Instant cutoff, Map<String, Long> deleted,
            long workspaceFiles, long objectFiles, long localLogFiles, List<String> errors) {
        boolean success() { return errors.isEmpty(); }
    }

    private final RetentionDatabaseCleaner database;
    private final Clock clock;
    private final Path workspaceRoot;
    private final Path objectRoot;
    private final Path localLogRoot;
    private final boolean localObjects;
    private final Consumer<Run> writer;
    private final AtomicBoolean running = new AtomicBoolean();

    RetentionJob(RetentionDatabaseCleaner database, Clock clock, Path workspaceRoot, Path objectRoot, Path localLogRoot,
            boolean localObjects, Consumer<Run> writer) {
        this.database = database;
        this.clock = clock;
        this.workspaceRoot = workspaceRoot;
        this.objectRoot = objectRoot;
        this.localLogRoot = localLogRoot;
        this.localObjects = localObjects;
        this.writer = writer;
    }

    /**
     * @description: 在单进程内防止重叠清理，各阶段失败仍保留其他阶段执行机会。
     * @return: 本次删除计数和错误摘要。
     * @author: yigeyige99
     */
    Run execute() {
        if (!running.compareAndSet(false, true)) throw new IllegalStateException("清理任务已在执行");
        try {
            Instant started = clock.instant();
            Instant cutoff = RetentionCatalog.cutoff(clock);
            Map<String, Long> deleted = Map.of();
            List<String> errors = new ArrayList<>();
            long workspaceFiles = 0;
            long objectFiles = 0;
            long localLogFiles = 0;
            try {
                RetentionDatabaseCleaner.Result result = database.clean(cutoff);
                deleted = result.deleted();
                errors.addAll(result.errors());
            } catch (RuntimeException exception) {
                errors.add("database:" + exception.getClass().getSimpleName());
            }
            if (workspaceRoot != null) {
                try { workspaceFiles = RetentionFileCleaner.cleanWorkspace(workspaceRoot, objectRoot, cutoff); }
                catch (Exception exception) { errors.add("workspace:" + exception.getClass().getSimpleName()); }
            }
            if (localObjects && objectRoot != null) {
                try { objectFiles = RetentionFileCleaner.cleanObjects(objectRoot, cutoff); }
                catch (Exception exception) { errors.add("objects:" + exception.getClass().getSimpleName()); }
            }
            if (localLogRoot != null) {
                try { localLogFiles = RetentionFileCleaner.cleanLocalLogs(localLogRoot, cutoff); }
                catch (Exception exception) { errors.add("local-logs:" + exception.getClass().getSimpleName()); }
            }
            Run run = new Run(started, clock.instant(), cutoff, deleted,
                    workspaceFiles, objectFiles, localLogFiles, List.copyOf(errors));
            writer.accept(run);
            return run;
        } finally {
            running.set(false);
        }
    }
}
