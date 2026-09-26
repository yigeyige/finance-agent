package cn.com.crc.ems.finance.agent.diagnostics;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/** Whitelists diagnostic log fields and redacts sensitive keys and values. */
public final class DiagnosticLogSanitizer {

    public static final String REDACTED = "[REDACTED]";
    private static final Set<String> ALLOWED_FIELDS = Set.of(
            "event", "component", "status", "reason", "durationMs", "outcome");
    private static final Pattern FORBIDDEN_KEY = Pattern.compile(
            "(?i).*(secret|token|password|credential|api[-_]?key|prompt|response|reasoning|sql|authorization).*" );
    private static final Pattern FORBIDDEN_VALUE = Pattern.compile(
            "(?i).*(sk-[a-z0-9_-]{8,}|password\\s*[=:]|bearer\\s+[a-z0-9._-]+|"
                    + "prompt\\s*[=:]|response\\s*[=:]|reasoning\\s*[=:]).*" );

    private DiagnosticLogSanitizer() {
    }

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
