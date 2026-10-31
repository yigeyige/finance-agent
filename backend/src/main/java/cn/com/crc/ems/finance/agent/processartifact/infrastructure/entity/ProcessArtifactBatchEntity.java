package cn.com.crc.ems.finance.agent.processartifact.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/** 流程文件批次持久化映射。 */
@TableName("task.process_artifact_batch")
public class ProcessArtifactBatchEntity {
    @TableId(value = "batch_id", type = IdType.INPUT)
    public String batchId;
    public UUID taskId;
    public UUID packetId;
    public Integer packetRevision;
    @TableField(value = "plan_json", typeHandler = JsonbTypeHandler.class)
    public String planJson;
    @TableField(value = "missing_items_json", typeHandler = JsonbTypeHandler.class)
    public String missingItemsJson;
    public String importStatus;
    public Instant createdAt;
}
