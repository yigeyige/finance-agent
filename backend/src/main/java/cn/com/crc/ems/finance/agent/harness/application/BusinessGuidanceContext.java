package cn.com.crc.ems.finance.agent.harness.application;

import java.util.UUID;
import java.util.List;

/**
 *
 * @Description: 业务处理初稿及会话修订送入模型前的受限上下文投影。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-08 21:10:00
 */
public record BusinessGuidanceContext(UUID taskId, String title, String description, String feedback,
        BusinessHandlingDraft currentDraft, BusinessSceneSkillContext sceneSkill,
        List<BusinessGuidanceAttachment> attachments, List<EmsDatabaseEvidence> databaseEvidence) {

    public BusinessGuidanceContext(UUID taskId, String title, String description, String feedback,
            BusinessHandlingDraft currentDraft) {
        this(taskId, title, description, feedback, currentDraft, null, List.of(), List.of());
    }

    public BusinessGuidanceContext(UUID taskId, String title, String description, String feedback,
            BusinessHandlingDraft currentDraft, BusinessSceneSkillContext sceneSkill) {
        this(taskId, title, description, feedback, currentDraft, sceneSkill, List.of(), List.of());
    }

    public BusinessGuidanceContext(UUID taskId, String title, String description, String feedback,
            BusinessHandlingDraft currentDraft, BusinessSceneSkillContext sceneSkill,
            List<BusinessGuidanceAttachment> attachments) {
        this(taskId, title, description, feedback, currentDraft, sceneSkill, attachments, List.of());
    }

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
        attachments = attachments == null ? List.of() : List.copyOf(attachments);
        if (attachments.size() > 3 || attachments.stream().mapToInt(item -> item.content().length()).sum() > 24_000) {
            throw new IllegalArgumentException("attachments are invalid");
        }
        databaseEvidence = databaseEvidence == null ? List.of() : List.copyOf(databaseEvidence);
        if (databaseEvidence.size() > 10) throw new IllegalArgumentException("databaseEvidence is invalid");
    }

    private static void requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
