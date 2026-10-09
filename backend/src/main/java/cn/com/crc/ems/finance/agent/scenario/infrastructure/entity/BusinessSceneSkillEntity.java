package cn.com.crc.ems.finance.agent.scenario.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 业务场景 Skill 的 MyBatis-Plus 持久化实体。 */
@TableName("task.business_scene_skill")
public class BusinessSceneSkillEntity {
    @TableId(value = "skill_id", type = IdType.ASSIGN_UUID)
    public String skillId;
    public String name;
    public String description;
    public String handlingType;
    public String premiseType;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
    public UUID deletedBy;
    public Instant deletedAt;
}
