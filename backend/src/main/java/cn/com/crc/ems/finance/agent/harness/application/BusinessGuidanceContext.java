package cn.com.crc.ems.finance.agent.harness.application;

import java.util.UUID;

/**
 *
 * @Description: 业务处理初稿及会话修订送入模型前的受限上下文投影。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-08 21:10:00
 */
public record BusinessGuidanceContext(UUID taskId, String title, String description, String feedback,
        BusinessHandlingDraft currentDraft) {

    public BusinessGuidanceContext {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        requireText(title, "title", 300);
        requireText(description, "description", 8000);
        if (feedback != null && (feedback.isBlank() || feedback.length() > 1000)) {
            throw new IllegalArgumentException("feedback is invalid");
        }
        if (feedback != null && currentDraft == null) {
            throw new IllegalArgumentException("currentDraft is required for feedback");
        }
    }

    private static void requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
