package cn.com.crc.ems.finance.agent.modelconfig.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 大模型连接配置的持久化实体，密钥只保存密文。 */
@TableName("governance.model_connection_config")
public class ModelConnectionConfigEntity {
    @TableId("config_id")
    public String configId;
    public String name;
    public String provider;
    public String apiUrl;
    public String modelName;
    public String reasoningEffort;
    public String apiKeyCiphertext;
    public boolean enabled;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
