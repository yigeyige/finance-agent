package cn.com.crc.ems.finance.agent.source.application;

import java.util.List;

/**
 *
 * @Description: 统一来源 SPI 的分页结果契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ScopePage<T>(List<T> records, int page, boolean hasNext, String nextToken,
        String sourceWatermark, String resultDigest) {

    public ScopePage {
        if (records == null || page < 1 || sourceWatermark == null || sourceWatermark.isBlank()
                || resultDigest == null || resultDigest.isBlank()
                || hasNext && (nextToken == null || nextToken.isBlank())) {
            throw new IllegalArgumentException("scope page is invalid");
        }
        records = List.copyOf(records);
    }
}
