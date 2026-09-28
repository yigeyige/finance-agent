package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.List;

import cn.com.crc.ems.finance.agent.evidence.domain.CoverageCompleteness;

/** 分页枚举器输出的覆盖度原始证据。 */
public record CoverageEnumerationResult<T>(List<CoveragePage<T>> pages, List<T> records, Long expectedTotal,
        long observedTotal, List<String> traversedPaths, List<String> uncoveredPaths, List<String> failures,
        String sourceWatermark, String resultSetDigest, CoverageCompleteness completeness) {

    public CoverageEnumerationResult {
        if (pages == null || records == null || traversedPaths == null || uncoveredPaths == null || failures == null
                || sourceWatermark == null || sourceWatermark.isBlank() || resultSetDigest == null
                || resultSetDigest.isBlank() || observedTotal < 0 || completeness == null) {
            throw new IllegalArgumentException("coverage enumeration result is invalid");
        }
        pages = List.copyOf(pages);
        records = List.copyOf(records);
        traversedPaths = List.copyOf(traversedPaths);
        uncoveredPaths = List.copyOf(uncoveredPaths);
        failures = List.copyOf(failures);
    }
}
