package cn.com.crc.ems.finance.agent.diagnostics;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:34
 */
public final class DiagnosticLogSanitizer {

    public static final String REDACTED = "[REDACTED]";
    private static final Set<String> ALLOWED_FIELDS = Set.of(
            "event", "component", "status", "reason", "durationMs", "outcome", "exceptionType", "databaseState");
    private static final Pattern FORBIDDEN_KEY = Pattern.compile(
            "(?i).*(secret|token|password|credential|api[-_]?key|prompt|response|reasoning|sql|authorization).*" );
    private static final Pattern FORBIDDEN_VALUE = Pattern.compile(
            "(?i).*(sk-[a-z0-9_-]{8,}|password\\s*[=:]|bearer\\s+[a-z0-9._-]+|"
                    + "prompt\\s*[=:]|response\\s*[=:]|reasoning\\s*[=:]).*" );

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @author: yigeyige99
     */
    private DiagnosticLogSanitizer() {
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: Map<String 输入参数。
     * @param: fields 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public static Map<String, String> sanitize(Map<String, ?> fields) {
        Map<String, String> safe = new LinkedHashMap<>();
        if (fields == null) {
            return safe;
        }
        fields.forEach((key, value) -> {
            if (key == null || key.isBlank()) {
                return;
            }
            String normalizedKey = key.trim();
            String text = value == null ? "null" : String.valueOf(value);
            if (!ALLOWED_FIELDS.contains(normalizedKey) || FORBIDDEN_KEY.matcher(normalizedKey).matches()
                    || FORBIDDEN_VALUE.matcher(text).matches()) {
                safe.put(normalizedKey, REDACTED);
            } else {
                safe.put(normalizedKey, text);
            }
        });
        return safe;
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: status 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public static String safeStatus(String status) {
        if (status == null) {
            return "UNKNOWN";
        }
        String normalized = status.toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "UP", "DOWN", "UNKNOWN" -> normalized;
            default -> "UNKNOWN";
        };
    }
}
