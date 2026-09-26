package cn.com.crc.ems.finance.agent.task.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 工具调用表的 MyBatis-Plus 持久化实体。 */
@TableName("task.tool_invocation")
public class ToolInvocationEntity {
    @TableId(value = "invocation_id", type = IdType.ASSIGN_UUID)
    public String invocationId;
    public UUID taskId;
    public UUID stepId;
    public UUID approvalId;
    public String toolCode;
    public String idempotencyKey;
    public String requestDigest;
    public String responseDigest;
    public String status;
    public Instant startedAt;
    public Instant completedAt;
    public String errorCode;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
