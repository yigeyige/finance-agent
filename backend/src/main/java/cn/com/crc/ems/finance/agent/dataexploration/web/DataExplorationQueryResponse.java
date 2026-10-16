package cn.com.crc.ems.finance.agent.dataexploration.web;

import java.util.List;

import cn.com.crc.ems.finance.agent.dataexploration.application.DataExplorationQueryResult;

/**
 *
 * @Description: 数据探索的受限查询结果响应，不包含 SQL 或连接信息。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-16 15:30:00
 */
public record DataExplorationQueryResponse(List<String> columns, List<List<String>> rows, int rowCount,
        long elapsedMillis) {
    public static DataExplorationQueryResponse from(DataExplorationQueryResult value) {
        return new DataExplorationQueryResponse(value.columns(), value.rows(), value.rowCount(), value.elapsedMillis());
    }
}
