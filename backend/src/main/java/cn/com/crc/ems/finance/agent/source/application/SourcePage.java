package cn.com.crc.ems.finance.agent.source.application;

import java.util.List;
import java.util.Map;

/** 合成源分页结果及覆盖度元数据。 */
public record SourcePage(List<Map<String, Object>> records, int page, int size, boolean hasNext,
        String sourceVersion, String resultDigest, String scenario) {

    public SourcePage {
        if (records == null || page < 1 || size < 1 || sourceVersion == null || resultDigest == null
                || scenario == null) {
            throw new IllegalArgumentException("source page is invalid");
        }
        records = records.stream().map(Map::copyOf).toList();
    }
}
