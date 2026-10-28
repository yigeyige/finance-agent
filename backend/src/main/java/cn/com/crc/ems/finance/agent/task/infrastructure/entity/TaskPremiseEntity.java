package cn.com.crc.ems.finance.agent.task.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: 任务前提表的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@TableName("task.task_premise")
public class TaskPremiseEntity {
    @TableId(value = "premise_id", type = IdType.ASSIGN_UUID)
    public String premiseId;
    public UUID taskId;
    public String subjectKey;
    public UUID providedBy;
    public Instant providedAt;
    public Boolean independentlyVerified;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
