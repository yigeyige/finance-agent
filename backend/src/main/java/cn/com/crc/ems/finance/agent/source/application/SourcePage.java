package cn.com.crc.ems.finance.agent.source.application;

import java.util.List;
import java.util.Map;

/**
 *
 * @Description: 合成源分页结果及覆盖度元数据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record SourcePage(List<Map<String, Object>> records, int page, int size, boolean hasNext,
        String sourceVersion, String resultDigest, String scenario, Long expectedTotal) {

    public SourcePage(List<Map<String, Object>> records, int page, int size, boolean hasNext,
            String sourceVersion, String resultDigest, String scenario) {
        this(records, page, size, hasNext, sourceVersion, resultDigest, scenario, null);
    }

    public SourcePage {
        if (records == null || page < 1 || size < 1 || sourceVersion == null || resultDigest == null
                || scenario == null || expectedTotal != null && expectedTotal < 0) {
            throw new IllegalArgumentException("source page is invalid");
        }
        records = records.stream().map(Map::copyOf).toList();
    }
}
