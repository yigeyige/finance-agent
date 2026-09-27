package cn.com.crc.ems.finance.agent.task.infrastructure.worker;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;
import cn.com.crc.ems.finance.agent.config.DataSourceRoleContext;
import cn.com.crc.ems.finance.agent.task.application.TaskLeaseClaim;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskLeaseService;

/**
 *
 * @Description: 使用独立有界线程池轮询 PostgreSQL 步骤租约并支持优雅停止的 Worker 生命周期。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-28 10:20:00
 */
public final class PostgresWorker implements SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresWorker.class);

    private final TaskLeaseService leaseService;
    private final WorkerStepHandler stepHandler;
    private final FinanceAgentProperties.Worker properties;
    private final AtomicBoolean acceptingClaims = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger threadSequence = new AtomicInteger();

    private ScheduledExecutorService scheduler;
    private ThreadPoolExecutor executor;

    public PostgresWorker(TaskLeaseService leaseService, WorkerStepHandler stepHandler,
            FinanceAgentProperties.Worker properties) {
        this.leaseService = leaseService;
        this.stepHandler = stepHandler;
        this.properties = properties;
    }

    /** 启动轮询器和独立 Worker 线程池。 */
    @Override
    public synchronized void start() {
        if (running.get()) {
            return;
        }
        executor = new ThreadPoolExecutor(properties.getWorkerCount(), properties.getWorkerCount(), 0L,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(properties.getQueueCapacity()),
                namedFactory("finance-agent-worker"), new ThreadPoolExecutor.AbortPolicy());
        scheduler = new ScheduledThreadPoolExecutor(1, namedFactory("finance-agent-worker-scheduler"));
        acceptingClaims.set(true);
        running.set(true);
        scheduler.scheduleWithFixedDelay(this::scheduleClaim, 0, properties.getPollIntervalMillis(),
                TimeUnit.MILLISECONDS);
    }

    /** 停止领取并等待已提交步骤处理器在宽限期内结束。 */
    @Override
    public synchronized void stop() {
        stop(() -> {
        });
    }

    /** Spring 关闭回调，禁止新领取后等待有界执行池排空。 */
    @Override
    public synchronized void stop(Runnable callback) {
        acceptingClaims.set(false);
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(properties.getShutdownGracePeriodMillis(), TimeUnit.MILLISECONDS)) {
                    LOGGER.warn("worker shutdown grace period elapsed; running steps may finish outside this process");
                }
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        running.set(false);
        callback.run();
    }

    /** 手工执行一次领取循环，供契约测试和后续调度扩展复用。 */
    public void runOnce() {
        if (!acceptingClaims.get()) {
            return;
        }
        DataSourceRoleContext.withWorker(() -> {
            leaseService.recoverExpired(properties.getOwnerInstance(), null, Instant.now(),
                    Duration.ofMillis(properties.getLeaseDurationMillis()), properties.getQueueCapacity());
            leaseService.claimNext(properties.getOwnerInstance(), null, Instant.now(),
                            Duration.ofMillis(properties.getLeaseDurationMillis()))
                    .ifPresent(this::executeClaim);
            return null;
        });
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    private void scheduleClaim() {
        if (!acceptingClaims.get()) {
            return;
        }
        try {
            executor.execute(this::runOnce);
        } catch (RejectedExecutionException ignored) {
            // 有界队列已满时跳过本轮，下一轮重新尝试，避免阻塞调度线程。
        }
    }

    private void executeClaim(TaskLeaseClaim claim) {
        try {
            stepHandler.execute(claim);
        } catch (RuntimeException failure) {
            LOGGER.warn("worker step processing failed, taskId={}, stepId={}", claim.lease().taskId(),
                    claim.lease().stepId());
        } finally {
            leaseService.release(claim.lease(), Instant.now(), null);
        }
    }

    private ThreadFactory namedFactory(String prefix) {
        return runnable -> {
            Thread thread = new Thread(runnable, prefix + "-" + threadSequence.incrementAndGet());
            thread.setDaemon(false);
            return thread;
        };
    }
}
