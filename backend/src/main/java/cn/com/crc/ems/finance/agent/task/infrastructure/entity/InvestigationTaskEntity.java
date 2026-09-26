package cn.com.crc.ems.finance.agent.task.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 调查任务表的 MyBatis-Plus 持久化实体。 */
@TableName("task.investigation_task")
public class InvestigationTaskEntity {
    @TableId(value = "task_id", type = IdType.INPUT)
    public UUID taskId;
    public String title;
    public String originalQuestion;
    public UUID initiatorUserId;
    public String status;
    public UUID runtimeBundleId;
    public UUID currentScopeRevisionId;
    public UUID currentStepId;
    public Instant createdAt;
    public Instant updatedAt;
    public Long rowVersion;
    public Instant closedAt;
    public UUID createdBy;
    public UUID updatedBy;
}
