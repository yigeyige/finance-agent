package cn.com.crc.ems.finance.agent.iam.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: IAM 身份表的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@TableName("iam.agent_user")
public class AgentUserEntity {
    @TableId(value = "user_id", type = IdType.ASSIGN_UUID)
    public String userId;
    public String authSubject;
    public String employeeNo;
    public String displayName;
    public String status;
    public Instant createdAt;
    public UUID createdBy;
    public Instant updatedAt;
    public UUID updatedBy;
}
