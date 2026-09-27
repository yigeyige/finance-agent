package cn.com.crc.ems.finance.agent.packet.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 决策包复核表的 MyBatis-Plus 持久化实体。 */
@TableName("packet.packet_review")
public class PacketReviewEntity {
    @TableId(value = "review_id", type = IdType.ASSIGN_UUID)
    public String reviewId;
    public UUID draftId;
    public UUID reviewerUserId;
    public String contentDigest;
    public UUID scopeRevisionId;
    public UUID runtimeBundleId;
    public String decision;
    public Instant reviewedAt;
    public String comment;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
