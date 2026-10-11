package cn.com.crc.ems.finance.agent.iam.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: IAM 角色授权表的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@TableName("iam.role_binding")
public class RoleBindingEntity {
    @TableId(value = "user_id", type = IdType.ASSIGN_UUID)
    public String userId;
    public String roleCode;
    public UUID grantedBy;
    public Instant grantedAt;
    public Instant revokedAt;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
