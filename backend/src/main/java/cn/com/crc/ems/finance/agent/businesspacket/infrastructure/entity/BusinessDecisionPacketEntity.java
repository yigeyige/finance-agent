package cn.com.crc.ems.finance.agent.businesspacket.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/**
 *
 * @Description: 业务决策包持久化实体，权威规范 JSON 以 jsonb 存证。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
@TableName("task.business_decision_packet")
public class BusinessDecisionPacketEntity {
    @TableId(value = "packet_id", type = IdType.ASSIGN_UUID)
    public String packetId;
    public UUID taskId;
    public Integer revision;
    @TableField(value = "canonical_content", typeHandler = JsonbTypeHandler.class)
    public String canonicalContent;
    public String contentDigest;
    public Instant createdAt;
}
