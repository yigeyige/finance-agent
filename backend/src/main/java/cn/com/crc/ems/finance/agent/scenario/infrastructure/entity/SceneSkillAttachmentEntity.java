package cn.com.crc.ems.finance.agent.scenario.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: 业务场景 Skill 附件的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-30 10:00:00
 */
@TableName("task.business_scene_skill_attachment")
public class SceneSkillAttachmentEntity {
    @TableId(value = "attachment_id", type = IdType.ASSIGN_UUID)
    public String attachmentId;
    public String skillId;
    public String fileName;
    public String contentType;
    public String summary;
    public String contentText;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
