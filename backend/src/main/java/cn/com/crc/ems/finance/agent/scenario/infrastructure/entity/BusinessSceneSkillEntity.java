package cn.com.crc.ems.finance.agent.scenario.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: 业务场景 Skill 的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@TableName("task.business_scene_skill")
public class BusinessSceneSkillEntity {
    @TableId(value = "skill_id", type = IdType.ASSIGN_UUID)
    public String skillId;
    public String name;
    public String investigationPrompt;
    public String decisionPacketPrompt;
    public String processArtifactPrompt;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
    public UUID deletedBy;
    public Instant deletedAt;
}
