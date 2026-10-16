package cn.com.crc.ems.finance.agent.dataexploration.web;

import java.util.List;

import cn.com.crc.ems.finance.agent.dataexploration.application.DataExplorationCatalog;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseTableMetadata;

/**
 *
 * @Description: 数据探索目录的无凭据响应视图。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-16 15:30:00
 */
public record DataExplorationCatalogResponse(String dataSourceName, int maxRows, List<EmsDatabaseTableMetadata> tables) {
    public static DataExplorationCatalogResponse from(DataExplorationCatalog value) {
        return new DataExplorationCatalogResponse(value.dataSourceName(), value.maxRows(), value.tables());
    }
}
