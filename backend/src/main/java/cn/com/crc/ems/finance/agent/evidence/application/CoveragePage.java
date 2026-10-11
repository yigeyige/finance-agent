package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.List;

/**
 *
 * @Description: 只读来源的一页覆盖度遍历结果。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record CoveragePage<T>(int pageNumber, List<T> records, Long expectedTotal, boolean hasNext,
        String nextToken, String sourceWatermark, String traversedPath) {

    public CoveragePage {
        if (pageNumber < 1 || records == null || expectedTotal != null && expectedTotal < 0
                || sourceWatermark == null || sourceWatermark.isBlank()
                || traversedPath == null || traversedPath.isBlank()) {
            throw new IllegalArgumentException("coverage page is invalid");
        }
        if (hasNext && (nextToken == null || nextToken.isBlank())) {
            throw new IllegalArgumentException("next token is required when more pages exist");
        }
        records = List.copyOf(records);
    }
}
