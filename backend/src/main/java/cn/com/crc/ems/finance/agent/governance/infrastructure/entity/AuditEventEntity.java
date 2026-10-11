package cn.com.crc.ems.finance.agent.governance.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/**
 *
 * @Description: 审计事件表的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@TableName("governance.audit_event")
public class AuditEventEntity {
    @TableId(value = "audit_event_id", type = IdType.ASSIGN_UUID)
    public String auditEventId;
    public UUID taskId;
    public UUID actorUserId;
    public String eventType;
    public String targetType;
    public String targetId;
    @TableField(value = "event_payload", typeHandler = JsonbTypeHandler.class)
    public String eventPayload;
    public String payloadDigest;
    public Instant occurredAt;
    public String prevEventDigest;
    public String eventDigest;
    public String canonicalizationVersion;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
