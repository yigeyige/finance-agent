package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.util.List;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;

/**
 *
 * @Description: EMS 源库的受控只读执行端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface EmsDatabaseQueryExecutor {
    EmsDatabaseQueryResult execute(EmsDatabaseConnectionConfig config, String sql);
    List<EmsDatabaseTableMetadata> listAllowedTables(EmsDatabaseConnectionConfig config);
    List<EmsDatabaseColumnMetadata> listColumns(EmsDatabaseConnectionConfig config, String schema, String table);
    boolean probe(EmsDatabaseConnectionConfig config);
}
