package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.util.List;
import java.util.Map;

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

    /** 使用命名参数执行只读查询；默认实现不支持参数化。 */
    default EmsDatabaseQueryResult execute(EmsDatabaseConnectionConfig config, String sql,
            Map<String, Object> parameters) {
        throw new UnsupportedOperationException("EMS 参数化查询未实现");
    }

    List<EmsDatabaseTableMetadata> listAllowedTables(EmsDatabaseConnectionConfig config);
    List<EmsDatabaseColumnMetadata> listColumns(EmsDatabaseConnectionConfig config, String schema, String table);
    EmsSchemaCatalog listConfigurationSchema(EmsDatabaseConnectionConfig config);
    boolean probe(EmsDatabaseConnectionConfig config);
}
