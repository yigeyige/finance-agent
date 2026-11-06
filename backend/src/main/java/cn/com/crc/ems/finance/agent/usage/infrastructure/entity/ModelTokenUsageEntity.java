package cn.com.crc.ems.finance.agent.usage.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @Description: 模型 token 用量表的 MyBatis-Plus 持久化实体。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 14:10:00
 */
@TableName("usage.model_token_usage")
public class ModelTokenUsageEntity {
    @TableId(value = "model_token_usage_id", type = IdType.ASSIGN_UUID)
    public String modelTokenUsageId;
    public UUID userId;
    public UUID taskId;
    public String modelName;
    public String mode;
    public Long tokens;
    public Instant occurredAt;
    public Instant createdAt;
}
