package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.util.List;

/**
 *
 * @Description: 已去除 JDBC 对象且限制行列长度的 EMS 只读查询结果。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record EmsDatabaseQueryResult(List<String> columns, List<List<String>> rows) {
    public EmsDatabaseQueryResult {
        columns = List.copyOf(columns);
        rows = rows.stream().map(List::copyOf).toList();
    }
}
