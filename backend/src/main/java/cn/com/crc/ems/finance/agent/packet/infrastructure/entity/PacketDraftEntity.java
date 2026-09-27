package cn.com.crc.ems.finance.agent.packet.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/** 决策包草稿表的 MyBatis-Plus 持久化实体。 */
@TableName("packet.packet_draft")
public class PacketDraftEntity {
    @TableId(value = "draft_id", type = IdType.ASSIGN_UUID)
    public String draftId;
    public UUID taskId;
    public Integer draftNo;
    public UUID scopeRevisionId;
    public UUID runtimeBundleId;
    @TableField(value = "canonical_content", typeHandler = JsonbTypeHandler.class)
    public String canonicalContent;
    public String contentDigest;
    public String status;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
