package cn.com.crc.ems.finance.agent.governance.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/** 本地 outbox 表的 MyBatis-Plus 持久化实体。 */
@TableName("governance.outbox_event")
public class OutboxEventEntity {
    @TableId(value = "outbox_id", type = IdType.ASSIGN_UUID)
    public String outboxId;
    public String aggregateType;
    public String aggregateId;
    public String eventType;
    public String idempotencyKey;
    @TableField(value = "payload", typeHandler = JsonbTypeHandler.class)
    public String payload;
    public Instant createdAt;
    public Instant deliveredAt;
    public Integer deliveryAttempts;
    public UUID createdBy;
    public UUID updatedBy;
    public Instant updatedAt;
}
