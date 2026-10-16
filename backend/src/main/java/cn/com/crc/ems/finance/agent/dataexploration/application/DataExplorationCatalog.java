package cn.com.crc.ems.finance.agent.dataexploration.application;

import java.util.List;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseTableMetadata;

/**
 *
 * @Description: 数据探索页面需要的受控数据源和表目录视图。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-16 15:30:00
 */
public record DataExplorationCatalog(String dataSourceName, int maxRows, List<EmsDatabaseTableMetadata> tables) {
    public DataExplorationCatalog {
        if (dataSourceName == null || dataSourceName.isBlank() || maxRows < 1) {
            throw new IllegalArgumentException("数据探索目录不合法");
        }
        tables = List.copyOf(tables);
    }
}
