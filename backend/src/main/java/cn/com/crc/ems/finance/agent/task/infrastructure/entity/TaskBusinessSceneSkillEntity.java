package cn.com.crc.ems.finance.agent.task.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: 任务选择的业务场景 Skill 关联实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@TableName("task.task_business_scene_skill")
public class TaskBusinessSceneSkillEntity {
    @TableId("task_id")
    public String taskId;
    public String skillId;
    public UUID createdBy;
    public Instant createdAt;
}
