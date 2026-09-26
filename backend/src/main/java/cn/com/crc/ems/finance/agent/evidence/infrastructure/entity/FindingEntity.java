package cn.com.crc.ems.finance.agent.evidence.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 判断结果表的 MyBatis-Plus 持久化实体。 */
@TableName("evidence.finding")
public class FindingEntity {
    @TableId(value = "finding_id", type = IdType.ASSIGN_UUID)
    public String findingId;
    public UUID taskId;
    public String findingType;
    public String statement;
    public String judgmentMode;
    public String effectiveClass;
    public UUID scopeRevisionId;
    public String status;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
