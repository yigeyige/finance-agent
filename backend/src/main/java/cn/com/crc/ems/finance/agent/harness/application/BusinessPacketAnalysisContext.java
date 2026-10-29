package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.UUID;

/**
 *
 * @Description: 决策包模型分析允许接收的固定输入：任务信息、去重后的已有证据与可选决策包提示词。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-28 10:00:00
 */
public record BusinessPacketAnalysisContext(UUID taskId, String title, String question,
        List<EmsDatabaseEvidence> evidence, String decisionPacketPrompt) {

    public BusinessPacketAnalysisContext {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        requireText(title, "title", 300);
        requireText(question, "question", 8000);
        if (evidence == null || evidence.isEmpty()) {
            throw new IllegalStateException("尚无可用证据，请先完成调查证据采集");
        }
        evidence = List.copyOf(evidence);
        if (decisionPacketPrompt != null && (decisionPacketPrompt.isBlank() || decisionPacketPrompt.length() > 8000)) {
            throw new IllegalArgumentException("decisionPacketPrompt is invalid");
        }
    }

    private static void requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
