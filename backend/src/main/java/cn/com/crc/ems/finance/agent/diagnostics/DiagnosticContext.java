package cn.com.crc.ems.finance.agent.diagnostics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.MDC;

/** Correlation context shared by request handling and structured logging. */
public final class DiagnosticContext {

    public static final String TRACE_ID = "traceId";
    public static final String TASK_ID = "taskId";
    public static final String STEP_ID = "stepId";
    public static final String INVOCATION_ID = "invocationId";

    private DiagnosticContext() {
    }

    public static String generatedId() {
        return UUID.randomUUID().toString();
    }

    public static Map<String, String> snapshot() {
        Map<String, String> snapshot = new LinkedHashMap<>();
        putIfPresent(snapshot, TRACE_ID);
        putIfPresent(snapshot, TASK_ID);
        putIfPresent(snapshot, STEP_ID);
        putIfPresent(snapshot, INVOCATION_ID);
        return snapshot;
    }

    private static void putIfPresent(Map<String, String> target, String key) {
        String value = MDC.get(key);
        if (value != null && !value.isBlank()) {
            target.put(key, value);
        }
    }
}
