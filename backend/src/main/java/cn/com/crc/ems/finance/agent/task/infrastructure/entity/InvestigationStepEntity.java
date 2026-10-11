package cn.com.crc.ems.finance.agent.task.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: 调查步骤表的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@TableName("task.investigation_step")
public class InvestigationStepEntity {
    @TableId(value = "step_id", type = IdType.ASSIGN_UUID)
    public String stepId;
    public UUID taskId;
    public Integer sequenceNo;
    public String goal;
    public Boolean recommended;
    public String status;
    public Instant createdAt;
    public Instant completedAt;
    public UUID createdBy;
    public Instant updatedAt;
    public UUID updatedBy;
}
