package cn.com.crc.ems.finance.agent.iam.infrastructure.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** IAM 用户密码凭据的 MyBatis-Plus 持久化实体。 */
@TableName("iam.user_password")
public class UserPasswordEntity {
    @TableId("user_id")
    public String userId;

    public byte[] passwordHash;
    public byte[] passwordSalt;
    public int iterations;
    public String algorithm;
    public Instant updatedAt;
}
