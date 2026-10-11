package cn.com.crc.ems.finance.agent.task.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: 步骤审批表的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@TableName("task.step_approval")
public class StepApprovalEntity {
    @TableId(value = "approval_id", type = IdType.ASSIGN_UUID)
    public String approvalId;
    public UUID stepId;
    public Integer revisionNo;
    public UUID approvedBy;
    public Instant approvedAt;
    public Instant expiresAt;
    public String goalHash;
    public UUID sourceBindingId;
    public UUID scopeRevisionId;
    public String fieldPolicyVersion;
    public String queryTemplateVersion;
    public Integer maxCalls;
    public Integer usedCalls;
    public String approvalDigest;
    public String status;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
