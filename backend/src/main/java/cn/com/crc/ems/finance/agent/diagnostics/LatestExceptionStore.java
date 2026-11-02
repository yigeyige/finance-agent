package cn.com.crc.ems.finance.agent.diagnostics;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

/**
 * @Description: 仅在当前进程保存最近一次失败的安全堆栈快照，不持有原始异常或业务数据。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-02 23:00:00
 */
@Component
public final class LatestExceptionStore {
    private static final int MAX_CAUSES = 4;
    private static final int MAX_FRAMES_PER_CAUSE = 80;
    private static final int MAX_STACK_LENGTH = 16_000;

    private final Clock clock;
    private final AtomicReference<Captured> latest = new AtomicReference<>();

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
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(failure, "failure");
        latest.set(capture(source, failure));
    }

    /** 模型异常穿出接口后可能再次到达兜底处理器，同一异常对象不重复改写来源。 */
    public void recordUnhandled(Throwable failure) {
        Objects.requireNonNull(failure, "failure");
        int identity = System.identityHashCode(failure);
        latest.updateAndGet(previous -> previous != null
                && previous.snapshot().source() == Source.MODEL_INTERACTION
                && previous.failureIdentity() == identity
                        ? previous : capture(Source.BUSINESS_API, failure));
    }

    /** 读取当前进程的最近快照；服务启动后尚无异常时返回空。 */
    public Snapshot latest() {
        Captured captured = latest.get();
        return captured == null ? null : captured.snapshot();
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
