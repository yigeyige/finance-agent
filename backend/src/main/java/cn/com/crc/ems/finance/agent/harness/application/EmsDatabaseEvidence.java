package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;

/** 已执行的 EMS 只读查询结果投影，仅作为需要人工核验的模型辅助信息。 */
public record EmsDatabaseEvidence(String purpose, List<String> columns, List<List<String>> rows) {
    public EmsDatabaseEvidence {
        if (purpose == null || purpose.isBlank() || purpose.length() > 300 || columns == null || columns.size() > 20
                || rows == null || rows.size() > 100) {
            throw new IllegalArgumentException("EMS 数据库辅助信息不合法");
        }
        columns = List.copyOf(columns);
        rows = rows.stream().map(List::copyOf).toList();
    }
}
