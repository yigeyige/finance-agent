package cn.com.crc.ems.finance.agent.evidence.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/** 事实快照表的 MyBatis-Plus 持久化实体。 */
@TableName("evidence.fact_snapshot")
public class FactSnapshotEntity {
    @TableId(value = "snapshot_id", type = IdType.ASSIGN_UUID)
    public String snapshotId;
    public UUID taskId;
    public UUID stepId;
    public UUID sourceBindingId;
    public String sourceObjectType;
    public String sourceObjectKey;
    @TableField(value = "allowed_fields", typeHandler = JsonbTypeHandler.class)
    public String allowedFields;
    public Instant checkedAt;
    public Instant effectiveFrom;
    public Instant effectiveTo;
    public String effectiveClass;
    public String sourceVersion;
    public String resultDigest;
    public String fieldPolicyVersion;
    public String schemaVersion;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
