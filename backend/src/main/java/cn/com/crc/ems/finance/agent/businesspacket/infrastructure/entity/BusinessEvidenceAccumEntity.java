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
 * @Description: 业务处理证据工作区持久化实体，当轮证据以 jsonb 存证。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
@TableName("task.business_evidence_accum")
public class BusinessEvidenceAccumEntity {
    @TableId(value = "evidence_accum_id", type = IdType.ASSIGN_UUID)
    public String evidenceAccumId;
    public UUID taskId;
    public Integer roundNo;
    @TableField(value = "evidence", typeHandler = JsonbTypeHandler.class)
    public String evidence;
    public Boolean snapshot;
    public Instant createdAt;
}
