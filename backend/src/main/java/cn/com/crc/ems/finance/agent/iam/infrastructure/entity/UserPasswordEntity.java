package cn.com.crc.ems.finance.agent.iam.infrastructure.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: IAM 用户密码凭据的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
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
