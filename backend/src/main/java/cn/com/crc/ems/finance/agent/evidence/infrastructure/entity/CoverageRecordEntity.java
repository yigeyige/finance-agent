package cn.com.crc.ems.finance.agent.evidence.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/** 覆盖记录表的 MyBatis-Plus 持久化实体。 */
@TableName("evidence.coverage_record")
public class CoverageRecordEntity {
    @TableId(value = "coverage_id", type = IdType.ASSIGN_UUID)
    public String coverageId;
    public UUID taskId;
    public UUID stepId;
    public UUID scopeRevisionId;
    public String queryTemplateVersion;
    public String queryConditionDigest;
    public String enumerationMethod;
    public Integer pageCount;
    public Long expectedTotal;
    public Long observedTotal;
    @TableField(value = "traversed_paths", typeHandler = JsonbTypeHandler.class)
    public String traversedPaths;
    @TableField(value = "uncovered_paths", typeHandler = JsonbTypeHandler.class)
    public String uncoveredPaths;
    @TableField(value = "failures", typeHandler = JsonbTypeHandler.class)
    public String failures;
    public String sourceWatermark;
    public String resultSetDigest;
    public String completeness;
    public Instant checkedAt;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
