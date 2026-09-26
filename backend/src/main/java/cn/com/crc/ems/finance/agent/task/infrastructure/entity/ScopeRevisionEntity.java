package cn.com.crc.ems.finance.agent.task.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/** 范围修订表的 MyBatis-Plus 持久化实体。 */
@TableName("task.scope_revision")
public class ScopeRevisionEntity {
    @TableId(value = "scope_revision_id", type = IdType.INPUT)
    public UUID scopeRevisionId;
    public UUID taskId;
    public Integer revisionNo;
    public String systemCode;
    @TableField(value = "object_selector", typeHandler = JsonbTypeHandler.class)
    public String objectSelector;
    @TableField(value = "effective_time_range", typeHandler = JsonbTypeHandler.class)
    public String effectiveTimeRange;
    @TableField(value = "included_paths", typeHandler = JsonbTypeHandler.class)
    public String includedPaths;
    @TableField(value = "excluded_paths", typeHandler = JsonbTypeHandler.class)
    public String excludedPaths;
    @TableField(value = "allowed_read_fields", typeHandler = JsonbTypeHandler.class)
    public String allowedReadFields;
    @TableField(value = "allowed_retain_fields", typeHandler = JsonbTypeHandler.class)
    public String allowedRetainFields;
    @TableField(value = "allowed_model_fields", typeHandler = JsonbTypeHandler.class)
    public String allowedModelFields;
    public UUID confirmedBy;
    public Instant confirmedAt;
    public String scopeDigest;
    public String schemaVersion;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
