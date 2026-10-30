package cn.com.crc.ems.finance.agent.scenario.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.scenario.domain.SceneSkillAttachment;

/**
 *
 * @Description: 业务场景 Skill 附件的查询与上传响应，不暴露附件正文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-30 10:00:00
 */
public record SceneSkillAttachmentResponse(UUID attachmentId, UUID skillId, String fileName, String contentType,
        String summary, Instant createdAt) {

    public static SceneSkillAttachmentResponse from(SceneSkillAttachment attachment) {
        return new SceneSkillAttachmentResponse(attachment.attachmentId(), attachment.skillId(), attachment.fileName(),
                attachment.contentType(), attachment.summary(), attachment.createdAt());
    }
}
