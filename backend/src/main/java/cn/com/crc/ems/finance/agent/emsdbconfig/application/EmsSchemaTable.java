package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.util.List;

/**
 * @Description: EMS 单张表的受控结构快照，仅含元数据。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 20:00:00
 */
public record EmsSchemaTable(String schema, String table, String comment, List<EmsDatabaseColumnMetadata> columns) {
    public EmsSchemaTable {
        columns = List.copyOf(columns);
        comment = comment == null ? "" : comment;
    }

    public String qualifiedName() { return schema + "." + table; }
    public EmsDatabaseTableMetadata metadata() { return new EmsDatabaseTableMetadata(schema, table, comment); }
}
