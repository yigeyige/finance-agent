package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @Description: 可送入下一次模型调用的压缩结构；摘要文本与 authoritative 引用严格分离。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ContextCompressionSummary(UUID taskId, Set<CompressionReference> references,
        Map<String, String> summaries, Set<String> unknownItems) {

    public ContextCompressionSummary {
        if (taskId == null || references == null || references.isEmpty()
                || summaries == null || unknownItems == null) {
            throw new IllegalArgumentException("compression summary is incomplete");
        }
        references = Set.copyOf(references);
        summaries = Map.copyOf(summaries);
        unknownItems = Set.copyOf(unknownItems);
        unknownItems.forEach(item -> requireText(item, "unknown item"));
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank() || value.length() > 500) {
            throw new IllegalArgumentException(field + " must be non-blank and at most 500 characters");
        }
    }
}
