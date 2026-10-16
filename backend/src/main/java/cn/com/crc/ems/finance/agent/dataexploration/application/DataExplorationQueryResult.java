package cn.com.crc.ems.finance.agent.dataexploration.application;

import java.util.List;

/**
 *
 * @Description: 不含 JDBC 对象和连接信息的数据探索受限查询结果。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-16 15:30:00
 */
public record DataExplorationQueryResult(List<String> columns, List<List<String>> rows, int rowCount, long elapsedMillis) {
    public DataExplorationQueryResult {
        columns = List.copyOf(columns);
        rows = rows.stream().map(List::copyOf).toList();
        if (rowCount != rows.size() || elapsedMillis < 0) throw new IllegalArgumentException("数据探索查询结果不合法");
    }
}
