package cn.com.crc.ems.finance.agent.emsdbconfig.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsSchemaCatalogRepository;

/**
 *
 * @Description: 不含密码或密文的 EMS 数据库连接安全视图。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record EmsDatabaseConnectionConfigResponse(UUID configId, String name, String jdbcUrl, String username,
        int connectTimeoutSeconds, int queryTimeoutSeconds, int maxRows, boolean enabled, boolean passwordConfigured,
        Instant updatedAt, Instant schemaSyncedAt, Integer schemaTableCount) {
    public static EmsDatabaseConnectionConfigResponse from(EmsDatabaseConnectionConfig value,
            EmsSchemaCatalogRepository.SyncStatus status) {
        return new EmsDatabaseConnectionConfigResponse(value.configId(), value.name(), value.jdbcUrl(), value.username(),
                value.connectTimeoutSeconds(), value.queryTimeoutSeconds(), value.maxRows(), value.enabled(),
                value.passwordCiphertext() != null && !value.passwordCiphertext().isBlank(), value.updatedAt(),
                status == null ? null : status.syncedAt(), status == null ? null : status.tableCount());
    }
}
