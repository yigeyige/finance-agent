package cn.com.crc.ems.finance.agent.emsdbconfig.application;

/**
 *
 * @Description: EMS 允许业务库中可安全展示的表结构摘要。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-16 15:30:00
 */
public record EmsDatabaseTableMetadata(String schema, String table, String comment) {
    public EmsDatabaseTableMetadata {
        if (schema == null || schema.isBlank() || table == null || table.isBlank()) {
            throw new IllegalArgumentException("EMS 表元数据不合法");
        }
        comment = comment == null ? "" : comment;
    }
}
