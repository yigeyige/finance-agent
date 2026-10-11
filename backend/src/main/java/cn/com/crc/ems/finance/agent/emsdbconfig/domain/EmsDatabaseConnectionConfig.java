package cn.com.crc.ems.finance.agent.emsdbconfig.domain;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

/**
 *
 * @Description: 平台管理员维护的 EMS MySQL 连接配置，密码仅保存密文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record EmsDatabaseConnectionConfig(UUID configId, String name, String jdbcUrl, String username,
        String passwordCiphertext, int connectTimeoutSeconds, int queryTimeoutSeconds, int maxRows, boolean enabled,
        UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public EmsDatabaseConnectionConfig {
        required(name, 100, "name");
        required(username, 200, "username");
        if (jdbcUrl == null || jdbcUrl.isBlank() || jdbcUrl.length() > 500
                || !jdbcUrl.toLowerCase(Locale.ROOT).startsWith("jdbc:mysql://")
                || jdbcUrl.matches("(?is).*([?&](user|password|allowLoadLocalInfile|allowUrlInLocalInfile|allowMultiQueries|autoDeserialize)=).*")) {
            throw new IllegalArgumentException("EMS 数据库地址不合法");
        }
        if (connectTimeoutSeconds < 1 || connectTimeoutSeconds > 30 || queryTimeoutSeconds < 1
                || queryTimeoutSeconds > 60 || maxRows < 1 || maxRows > 100) {
            throw new IllegalArgumentException("EMS 查询限制不合法");
        }
    }

    private static void required(String value, int maxLength, String field) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
