package cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: EMS 数据库连接配置的持久化实体，密码列仅保存密文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@TableName("governance.ems_database_connection_config")
public class EmsDatabaseConnectionConfigEntity {
    @TableId("config_id") public String configId;
    public String name;
    public String jdbcUrl;
    public String username;
    public String passwordCiphertext;
    public int connectTimeoutSeconds;
    public int queryTimeoutSeconds;
    public int maxRows;
    public boolean enabled;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
