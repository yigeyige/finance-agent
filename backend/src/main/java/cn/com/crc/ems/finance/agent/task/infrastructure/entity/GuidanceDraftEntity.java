package cn.com.crc.ems.finance.agent.task.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/**
 *
 * @Description: 业务处理草稿持久化实体，草稿全文以 jsonb 存证。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-20 22:00:00
 */
@TableName("task.guidance_draft")
public class GuidanceDraftEntity {
    @TableId(value = "guidance_draft_id", type = IdType.ASSIGN_UUID)
    public String guidanceDraftId;
    public UUID taskId;
    @TableField(value = "draft_json", typeHandler = JsonbTypeHandler.class)
    public String draftJson;
    public UUID createdBy;
    public Instant createdAt;
}
