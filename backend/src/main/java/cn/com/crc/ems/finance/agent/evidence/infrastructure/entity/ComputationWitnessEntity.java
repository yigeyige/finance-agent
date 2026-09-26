package cn.com.crc.ems.finance.agent.evidence.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/** 计算见证表的 MyBatis-Plus 持久化实体。 */
@TableName("evidence.computation_witness")
public class ComputationWitnessEntity {
    @TableId(value = "witness_id", type = IdType.ASSIGN_UUID)
    public String witnessId;
    public UUID findingId;
    public String algorithmVersion;
    public String ruleSetVersion;
    public String inputDigest;
    public String outputDigest;
    @TableField(value = "snapshot_ids", typeHandler = JsonbTypeHandler.class)
    public String snapshotIds;
    public Instant computedAt;
    public String recomputeStatus;
    public Instant recomputedAt;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
