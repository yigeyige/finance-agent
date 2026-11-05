package cn.com.crc.ems.finance.agent.diagnostics;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

/**
 * @Description: 在当前进程保存全站及个人最近失败的安全堆栈快照，不持有原始异常或业务数据。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-02 23:00:00
 */
@Component
public final class LatestExceptionStore {
    private static final int MAX_USERS = 256;
    private static final int MAX_CAUSES = 4;
    private static final int MAX_FRAMES_PER_CAUSE = 80;
    private static final int MAX_STACK_LENGTH = 16_000;

    private final Clock clock;
    private final AtomicReference<Captured> latest = new AtomicReference<>();
    private final Map<UUID, Snapshot> personal = new LinkedHashMap<>();

    public LatestExceptionStore(Clock clock) {
        this.clock = clock;
    }

    /**
     * @description: 将符合范围的失败转换为有界堆栈后覆盖前一条，不保存异常消息和原始对象。
     * @param: source 异常来源
     * @param: failure 本次最终失败
     * @author: yigeyige99
     */
    public void record(Source source, Throwable failure) {
        record(source, failure, null);
    }

    /** 按可信用户保存个人快照，同时更新管理员可见的全站快照。 */
    public void record(Source source, Throwable failure, UUID userId) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(failure, "failure");
        Captured captured = capture(source, failure);
        latest.set(captured);
        savePersonal(userId, captured.snapshot());
    }

    /** 模型异常穿出接口后可能再次到达兜底处理器，同一异常对象不重复改写来源。 */
    public void recordUnhandled(Throwable failure) {
        recordUnhandled(failure, null);
    }

    /** 未处理接口异常归属当前可信用户；模型端口已记录的同一异常不重复覆盖。 */
    public void recordUnhandled(Throwable failure, UUID userId) {
        Objects.requireNonNull(failure, "failure");
        int identity = System.identityHashCode(failure);
        Captured previous = latest.get();
        if (previous != null
                && previous.snapshot().source() == Source.MODEL_INTERACTION
                && previous.failureIdentity() == identity) {
            savePersonal(userId, previous.snapshot());
            return;
        }
        record(Source.BUSINESS_API, failure, userId);
    }

    /** 读取当前进程的最近快照；服务启动后尚无异常时返回空。 */
    public Snapshot latest() {
        Captured captured = latest.get();
        return captured == null ? null : captured.snapshot();
    }

    /** 仅返回指定用户的最近快照，后台和其他用户的异常不可见。 */
    public Snapshot latestFor(UUID userId) {
        if (userId == null) return null;
        synchronized (personal) { return personal.get(userId); }
    }

    private void savePersonal(UUID userId, Snapshot snapshot) {
        if (userId == null) return;
        synchronized (personal) {
            personal.remove(userId);
            personal.put(userId, snapshot);
            if (personal.size() > MAX_USERS) personal.remove(personal.keySet().iterator().next());
        }
    }

    private Captured capture(Source source, Throwable failure) {
        return new Captured(new Snapshot(Instant.now(clock), source, failure.getClass().getName(), formatStack(failure)),
                System.identityHashCode(failure));
    }

    /** 仅拼接类型与代码位置，异常消息可能包含敏感原文，始终排除。 */
    private static String formatStack(Throwable failure) {
        StringBuilder stack = new StringBuilder();
        Throwable current = failure;
        for (int cause = 0; current != null && cause < MAX_CAUSES && stack.length() < MAX_STACK_LENGTH; cause++) {
            append(stack, (cause == 0 ? "" : "Caused by: ") + current.getClass().getName() + "\n");
            StackTraceElement[] frames = current.getStackTrace();
            for (int index = 0; index < Math.min(frames.length, MAX_FRAMES_PER_CAUSE)
                    && stack.length() < MAX_STACK_LENGTH; index++) {
                StackTraceElement frame = frames[index];
                append(stack, "  at " + frame.getClassName() + "." + frame.getMethodName() + "("
                        + frame.getFileName() + ":" + frame.getLineNumber() + ")\n");
            }
            if (current.getCause() == current) {
                break;
            }
            current = current.getCause();
        }
        return stack.toString();
    }

    private static void append(StringBuilder stack, String line) {
        int remaining = MAX_STACK_LENGTH - stack.length();
        if (remaining > 0) {
            stack.append(line, 0, Math.min(remaining, line.length()));
        }
    }

    public enum Source { BUSINESS_API, MODEL_INTERACTION }

    public record Snapshot(Instant occurredAt, Source source, String exceptionType, String stackTrace) { }

    private record Captured(Snapshot snapshot, int failureIdentity) { }
}
