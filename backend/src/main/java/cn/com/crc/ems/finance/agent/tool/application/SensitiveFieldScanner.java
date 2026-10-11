package cn.com.crc.ems.finance.agent.tool.application;

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @Description: 递归扫描禁止进入持久化、日志或模型边界的字段名。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class SensitiveFieldScanner {

    private static final Set<String> FORBIDDEN = Set.of("raw_response", "source_raw", "password", "secret",
            "token", "access_token", "credential", "prompt", "reasoning", "reasoning_content");

    private SensitiveFieldScanner() { }

    public static void requireSafe(JsonNode value) {
        if (value == null) {
            throw new IllegalArgumentException("payload is required");
        }
        scan(value);
    }

    private static void scan(JsonNode node) {
        if (node.isObject()) {
            Iterator<String> names = node.fieldNames();
            while (names.hasNext()) {
                String originalName = names.next();
                String name = originalName.toLowerCase(Locale.ROOT);
                if (FORBIDDEN.contains(name)) {
                    throw new IllegalArgumentException("payload contains forbidden field");
                }
                scan(node.get(originalName));
            }
        } else if (node.isArray()) {
            node.forEach(SensitiveFieldScanner::scan);
        }
    }
}
