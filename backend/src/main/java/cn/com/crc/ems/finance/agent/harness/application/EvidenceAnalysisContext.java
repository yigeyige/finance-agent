package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.UUID;

/**
 *
 * @Description: 证据分析模型允许接收的脱敏统计、覆盖状态和判断陈述。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record EvidenceAnalysisContext(UUID taskId, String title, String question,
        int currentCount, int futureCount, int undeterminedCount,
        List<String> coverageStates, List<String> findingStatements) {

    public EvidenceAnalysisContext {
        if (taskId == null || currentCount < 0 || futureCount < 0 || undeterminedCount < 0) {
            throw new IllegalArgumentException("evidence analysis identity and counts are invalid");
        }
        requireText(title, "title", 300);
        requireText(question, "question", 8000);
        coverageStates = checkedList(coverageStates, "coverageStates", 50, 200);
        findingStatements = checkedList(findingStatements, "findingStatements", 100, 2000);
        if (coverageStates.isEmpty() || findingStatements.isEmpty()
                || currentCount + futureCount + undeterminedCount == 0) {
            throw new IllegalStateException("调查证据尚未准备完整，请先完成证据采集");
        }
    }

    private static List<String> checkedList(List<String> values, String field, int maxSize, int maxLength) {
        if (values == null || values.size() > maxSize || values.stream()
                .anyMatch(value -> value == null || value.isBlank() || value.length() > maxLength)) {
            throw new IllegalArgumentException(field + " is invalid");
        }
        return List.copyOf(values);
    }

    private static void requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
