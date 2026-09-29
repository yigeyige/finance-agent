package cn.com.crc.ems.finance.agent.packet.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 发布前范围重查记录的 MyBatis-Plus 持久化实体。 */
@TableName("packet.scope_recheck")
public class ScopeRecheckEntity {
    @TableId(value = "recheck_id", type = IdType.ASSIGN_UUID)
    public String recheckId;
    public UUID taskId;
    public UUID scopeRevisionId;
    public Instant checkedAt;
    public String decision;
    public String resultDigest;
    public String diffSummary;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
