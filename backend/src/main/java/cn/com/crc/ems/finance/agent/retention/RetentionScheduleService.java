package cn.com.crc.ems.finance.agent.retention;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

/**
 * @Description: 管理业务数据清理任务的持久频率与时间，并在修改后立即重排下一次触发。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 20:00:00
 */
public final class RetentionScheduleService {
    public record Job(String code, String name, String javaClass, String frequency,
            Integer dayOfWeek, Integer dayOfMonth, String time, String zone) { }

    static final String JOB_CODE = "retention-cleanup";
    private static final String JOB_NAME = "业务数据清理";
    private static final Logger LOG = LoggerFactory.getLogger(RetentionScheduleService.class);
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final String[] DAYS = { "", "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN" };

    private final RetentionScheduleStore store;
    private final TaskScheduler scheduler;
    private final Runnable task;
    private final AtomicBoolean manualQueued = new AtomicBoolean();
    private ScheduledFuture<?> pending;

    RetentionScheduleService(RetentionScheduleStore store, TaskScheduler scheduler, Runnable task) {
        this.store = store;
        this.scheduler = scheduler;
        this.task = task;
    }

    /**
     * @description: 应用就绪后从文件恢复调度，不补跑历史错过的执行。
     * @throws: IOException 调度配置不可读取。
     * @author: yigeyige99
     */
    @EventListener(ApplicationReadyEvent.class)
    public synchronized void start() throws IOException { reschedule(store.read()); }

    /**
     * @description: 读取管理员页面展示的固定清理任务设置。
     * @return: 当前调度频率和执行时间。
     * @throws: IOException 配置读取失败。
     * @author: yigeyige99
     */
    public Job current() throws IOException { return view(store.read()); }

    /**
     * @description: 持久保存有效的调度频率和时间，并取消旧触发后立即重排。
     * @param: schedule 新的频率及执行时间。
     * @return: 更新后的任务设置。
     * @throws: IOException 配置写入失败。
     * @author: yigeyige99
     */
    public synchronized Job update(RetentionScheduleStore.Schedule schedule) throws IOException {
        store.write(schedule);
        reschedule(schedule);
        LOG.info("清理任务执行时间已更新：频率{} 星期{} 月日期{} 时间{} {}",
                schedule.frequency(), schedule.dayOfWeek(), schedule.dayOfMonth(), schedule.time(), ZONE);
        return view(schedule);
    }

    /**
     * @description: 在现有单线程调度器中提交一次清理，不改变持久调度设置；手动提交尚未结束时拒绝重复排队。
     * @return: true 表示已提交，false 表示已有手动任务待执行或正在执行。
     * @author: yigeyige99
     */
    public boolean runOnce() {
        if (!manualQueued.compareAndSet(false, true)) return false;
        try {
            ScheduledFuture<?> future = scheduler.schedule(() -> {
                try { task.run(); }
                catch (RuntimeException exception) { LOG.error("手动清理任务执行失败", exception); }
                finally { manualQueued.set(false); }
            }, Instant.now());
            if (future == null) throw new IllegalStateException("无法安排手动清理任务");
            return true;
        } catch (RuntimeException exception) {
            manualQueued.set(false);
            throw exception;
        }
    }

    private void reschedule(RetentionScheduleStore.Schedule schedule) {
        ScheduledFuture<?> replacement = scheduler.schedule(task, new CronTrigger(cron(schedule), ZONE));
        if (replacement == null) throw new IllegalStateException("无法安排清理任务");
        ScheduledFuture<?> previous = pending;
        pending = replacement;
        if (previous != null) previous.cancel(false);
    }

    private static Job view(RetentionScheduleStore.Schedule schedule) {
        return new Job(JOB_CODE, JOB_NAME, RetentionJob.class.getName(), schedule.frequency().name(),
                schedule.dayOfWeek(), schedule.dayOfMonth(), schedule.time(), ZONE.getId());
    }

    static String cron(RetentionScheduleStore.Schedule schedule) {
        String[] parts = schedule.time().split(":");
        String prefix = "0 " + Integer.parseInt(parts[1]) + " " + Integer.parseInt(parts[0]);
        return switch (schedule.frequency()) {
            case DAILY -> prefix + " * * *";
            case WEEKLY -> prefix + " * * " + DAYS[schedule.dayOfWeek()];
            case MONTHLY -> prefix + " " + schedule.dayOfMonth() + " * *";
        };
    }
}
