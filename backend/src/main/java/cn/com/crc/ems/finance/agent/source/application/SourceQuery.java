package cn.com.crc.ems.finance.agent.source.application;

import java.util.Map;

/**
 *
 * @Description: 已注册源查询模板的参数。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record SourceQuery(String templateCode, Map<String, String> parameters) {

    public SourceQuery {
        if (templateCode == null || templateCode.isBlank() || parameters == null) {
            throw new IllegalArgumentException("source query template and parameters are required");
        }
        parameters = Map.copyOf(parameters);
    }
}
