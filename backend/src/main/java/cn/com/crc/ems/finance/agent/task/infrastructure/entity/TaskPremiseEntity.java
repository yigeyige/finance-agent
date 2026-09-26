package cn.com.crc.ems.finance.agent.task.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 任务前提表的 MyBatis-Plus 持久化实体。 */
@TableName("task.task_premise")
public class TaskPremiseEntity {
    @TableId(value = "premise_id", type = IdType.ASSIGN_UUID)
    public String premiseId;
    public UUID taskId;
    public String premiseType;
    public String subjectKey;
    public String statement;
    public UUID providedBy;
    public String sourceDescription;
    public Instant providedAt;
    public Boolean independentlyVerified;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
