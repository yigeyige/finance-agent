package cn.com.crc.ems.finance.agent.source.application;

import java.util.Map;

/** 已通过来源目录和字段策略批准的只读查询。 */
public record ApprovedQuery(String catalogVersion, String templateCode, Map<String, String> parameters) {

    public ApprovedQuery {
        if (catalogVersion == null || catalogVersion.isBlank() || templateCode == null || templateCode.isBlank()
                || parameters == null) {
            throw new IllegalArgumentException("approved query is invalid");
        }
        parameters = Map.copyOf(parameters);
    }
}
