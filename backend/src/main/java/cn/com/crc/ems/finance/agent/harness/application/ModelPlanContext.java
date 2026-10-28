package cn.com.crc.ems.finance.agent.harness.application;

import java.util.UUID;

/**
 *
 * @Description: 调查方案模型允许接收的任务与已确认范围投影。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ModelPlanContext(UUID taskId, String title, String question, UUID scopeRevisionId,
        String objectSelector) {

    public ModelPlanContext {
        if (taskId == null || scopeRevisionId == null) {
            throw new IllegalArgumentException("task and scope identity are required");
        }
        requireText(title, "title", 300);
        requireText(question, "question", 8000);
        requireJson(objectSelector, "objectSelector");
    }

    private static void requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }

    private static void requireJson(String value, String field) {
        if (value == null || value.isBlank() || value.length() > 12000) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
