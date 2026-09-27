package cn.com.crc.ems.finance.agent.packet.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/** 已发布决策包修订表的 MyBatis-Plus 持久化实体。 */
@TableName("packet.packet_revision")
public class PacketRevisionEntity {
    @TableId(value = "packet_revision_id", type = IdType.ASSIGN_UUID)
    public String packetRevisionId;
    public UUID taskId;
    public Integer revisionNo;
    public UUID draftId;
    @TableField(value = "canonical_content", typeHandler = JsonbTypeHandler.class)
    public String canonicalContent;
    public String htmlSnapshot;
    public String contentDigest;
    public Instant checkedAt;
    @TableField(value = "source_versions", typeHandler = JsonbTypeHandler.class)
    public String sourceVersions;
    @TableField(value = "coverage_summary", typeHandler = JsonbTypeHandler.class)
    public String coverageSummary;
    public UUID publishedBy;
    public Instant publishedAt;
    public UUID supersedesRevisionId;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
