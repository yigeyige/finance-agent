package cn.com.crc.ems.finance.agent.task.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: 任务租约表的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@TableName("task.task_lease")
public class TaskLeaseEntity {
    @TableId(value = "lease_id", type = IdType.ASSIGN_UUID)
    public String leaseId;
    public UUID taskId;
    public UUID stepId;
    public String ownerInstance;
    public Instant acquiredAt;
    public Instant heartbeatAt;
    public Instant expiresAt;
    public Instant releasedAt;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
