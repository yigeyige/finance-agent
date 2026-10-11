package cn.com.crc.ems.finance.agent.harness.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/**
 *
 * @Description: Harness checkpoint 表的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@TableName("harness.checkpoint")
public class CheckpointEntity {
    @TableId(value = "checkpoint_id", type = IdType.ASSIGN_UUID)
    public String checkpointId;
    public UUID sessionId;
    public UUID taskId;
    public UUID userId;
    public UUID stepId;
    public UUID scopeRevisionId;
    public UUID approvalId;
    public UUID runtimeBundleId;
    public String checkpointType;
    public String stateSchemaVersion;
    @TableField(value = "state_ref", typeHandler = JsonbTypeHandler.class)
    public String stateRef;
    public String stateDigest;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
