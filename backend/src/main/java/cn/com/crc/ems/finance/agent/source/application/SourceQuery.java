package cn.com.crc.ems.finance.agent.source.application;

import java.util.Map;

/** 已注册源查询模板的参数。 */
public record SourceQuery(String templateCode, Map<String, String> parameters) {

    public SourceQuery {
        if (templateCode == null || templateCode.isBlank() || parameters == null) {
            throw new IllegalArgumentException("source query template and parameters are required");
        }
        parameters = Map.copyOf(parameters);
    }
}
