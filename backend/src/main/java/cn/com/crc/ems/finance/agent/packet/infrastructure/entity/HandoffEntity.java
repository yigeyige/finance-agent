package cn.com.crc.ems.finance.agent.packet.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 决策包交接表的 MyBatis-Plus 持久化实体。 */
@TableName("packet.handoff")
public class HandoffEntity {
    @TableId(value = "handoff_id", type = IdType.ASSIGN_UUID)
    public String handoffId;
    public UUID packetRevisionId;
    public String ownerReference;
    public String externalProcessReference;
    public String downstreamStatus;
    public UUID handedOffBy;
    public Instant handedOffAt;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
