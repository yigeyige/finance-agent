package cn.com.crc.ems.finance.agent.scenario.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 业务场景 Skill 的可复用知识附件，保存文本投影与管理员填写的摘要。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-30 10:00:00
 */
public record SceneSkillAttachment(UUID attachmentId, UUID skillId, String fileName, String contentType,
        String summary, String contentText, UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public SceneSkillAttachment {
        if (attachmentId == null || skillId == null) {
            throw new IllegalArgumentException("attachment identity is required");
        }
        if (createdBy == null || createdAt == null || updatedBy == null || updatedAt == null) {
            throw new IllegalArgumentException("attachment audit fields are required");
        }
        if (fileName == null || fileName.isBlank() || fileName.length() > 255) {
            throw new IllegalArgumentException("attachment fileName is invalid");
        }
        if (contentType == null || contentType.isBlank() || contentType.length() > 100) {
            throw new IllegalArgumentException("attachment contentType is invalid");
        }
        if (summary == null || summary.isBlank() || summary.length() > 500) {
            throw new IllegalArgumentException("attachment summary is invalid");
        }
        if (contentText == null || contentText.isBlank()) {
            throw new IllegalArgumentException("attachment contentText is invalid");
        }
    }
}
