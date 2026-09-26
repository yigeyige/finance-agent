package cn.com.crc.ems.finance.agent.diagnostics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.MDC;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:26
 */
public final class DiagnosticContext {

    public static final String TRACE_ID = "traceId";
    public static final String TASK_ID = "taskId";
    public static final String STEP_ID = "stepId";
    public static final String INVOCATION_ID = "invocationId";

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @author: yigeyige99
     */
    private DiagnosticContext() {
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public static String generatedId() {
        return UUID.randomUUID().toString();
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public static Map<String, String> snapshot() {
        Map<String, String> snapshot = new LinkedHashMap<>();
        putIfPresent(snapshot, TRACE_ID);
        putIfPresent(snapshot, TASK_ID);
        putIfPresent(snapshot, STEP_ID);
        putIfPresent(snapshot, INVOCATION_ID);
        return snapshot;
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: Map<String 输入参数。
     * @param: target 输入参数。
     * @param: key 输入参数。
     * @author: yigeyige99
     */
    private static void putIfPresent(Map<String, String> target, String key) {
        String value = MDC.get(key);
        if (value != null && !value.isBlank()) {
            target.put(key, value);
        }
    }
}
