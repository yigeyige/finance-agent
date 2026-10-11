package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;

/** EMS 源库的受控只读执行端口。 */
public interface EmsDatabaseQueryExecutor {
    EmsDatabaseQueryResult execute(EmsDatabaseConnectionConfig config, String sql);
    boolean probe(EmsDatabaseConnectionConfig config);
}
