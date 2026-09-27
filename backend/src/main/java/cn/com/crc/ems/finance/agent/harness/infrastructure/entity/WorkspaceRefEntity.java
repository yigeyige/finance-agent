package cn.com.crc.ems.finance.agent.harness.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** Harness workspace 引用表的 MyBatis-Plus 持久化实体。 */
@TableName("harness.workspace_ref")
public class WorkspaceRefEntity {
    @TableId(value = "workspace_ref_id", type = IdType.ASSIGN_UUID)
    public String workspaceRefId;
    public UUID sessionId;
    public UUID taskId;
    public UUID userId;
    public String relativePath;
    public String objectRef;
    public String contentDigest;
    public String mediaType;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
