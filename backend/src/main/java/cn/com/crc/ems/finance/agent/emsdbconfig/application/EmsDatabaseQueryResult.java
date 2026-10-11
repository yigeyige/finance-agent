package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.util.List;

/** 已去除 JDBC 对象且限制行列长度的 EMS 只读查询结果。 */
public record EmsDatabaseQueryResult(List<String> columns, List<List<String>> rows) {
    public EmsDatabaseQueryResult {
        columns = List.copyOf(columns);
        rows = rows.stream().map(List::copyOf).toList();
    }
}
