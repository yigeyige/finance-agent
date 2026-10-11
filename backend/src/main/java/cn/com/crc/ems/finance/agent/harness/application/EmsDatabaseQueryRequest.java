package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

/** 模型提出、但尚未通过 SQL 守卫的 EMS 数据库查询建议。 */
public record EmsDatabaseQueryRequest(String purpose, String sql) {
    public EmsDatabaseQueryRequest {
        if (purpose == null || purpose.isBlank() || purpose.length() > 300 || sql == null || sql.isBlank()
                || sql.length() > 4_000) {
            throw new IllegalArgumentException("EMS 数据库查询建议不合法");
        }
    }

    /** 从模型严格 JSON 解析可选查询建议；false 表示当前信息不足而不查询。 */
    public static Optional<EmsDatabaseQueryRequest> fromJson(JsonNode node) {
        if (node == null || !node.path("needQuery").asBoolean(false)) return Optional.empty();
        return Optional.of(new EmsDatabaseQueryRequest(required(node, "purpose"), required(node, "sql")));
    }

    private static String required(JsonNode node, String name) {
        String value = node.path(name).asText();
        if (value == null || value.isBlank()) throw new IllegalArgumentException("模型返回的 EMS 查询建议缺少 " + name);
        return value;
    }
}
