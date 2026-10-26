package cn.com.crc.ems.finance.agent.emsdbconfig.application;

/**
 *
 * @Description: EMS 允许业务库中可安全展示的表结构摘要，含可选的表知识提示词。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-16 15:30:00
 */
public record EmsDatabaseTableMetadata(String schema, String table, String comment, String promptHint) {
    public EmsDatabaseTableMetadata {
        if (schema == null || schema.isBlank() || table == null || table.isBlank()) {
            throw new IllegalArgumentException("EMS 表元数据不合法");
        }
        comment = comment == null ? "" : comment;
        promptHint = promptHint == null ? "" : promptHint;
    }

    public EmsDatabaseTableMetadata(String schema, String table, String comment) {
        this(schema, table, comment, "");
    }
}
