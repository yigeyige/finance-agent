package cn.com.crc.ems.finance.agent.iam.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** IAM 身份表的 MyBatis-Plus 持久化实体。 */
@TableName("iam.agent_user")
public class AgentUserEntity {
    @TableId(value = "user_id", type = IdType.INPUT)
    public UUID userId;
    public String authSubject;
    public String employeeNo;
    public String displayName;
    public String status;
    public Instant createdAt;
    public UUID createdBy;
    public Instant updatedAt;
    public UUID updatedBy;
}
