package cn.com.crc.ems.finance.agent.harness.application;

import java.util.UUID;

/** 调查方案模型允许接收的任务与已确认范围投影。 */
public record ModelPlanContext(UUID taskId, String title, String question, UUID scopeRevisionId,
        String systemCode, String objectSelector, String effectiveTimeRange, String includedPaths) {

    public ModelPlanContext {
        if (taskId == null || scopeRevisionId == null) {
            throw new IllegalArgumentException("task and scope identity are required");
        }
        requireText(title, "title", 300);
        requireText(question, "question", 8000);
        requireText(systemCode, "systemCode", 200);
        requireJson(objectSelector, "objectSelector");
        requireJson(effectiveTimeRange, "effectiveTimeRange");
        requireJson(includedPaths, "includedPaths");
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
