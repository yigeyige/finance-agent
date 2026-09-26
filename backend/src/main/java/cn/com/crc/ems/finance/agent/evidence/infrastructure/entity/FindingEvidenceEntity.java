package cn.com.crc.ems.finance.agent.evidence.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 判断证据引用表的 MyBatis-Plus 持久化实体。 */
@TableName("evidence.finding_evidence")
public class FindingEvidenceEntity {
    @TableId(value = "finding_id", type = IdType.INPUT)
    public UUID findingId;
    public UUID snapshotId;
    public String relationType;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
