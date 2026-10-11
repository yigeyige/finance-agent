package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.evidence.domain.CoverageCompleteness;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/**
 *
 * @Description: 在有界页数内遍历来源并收集覆盖度证据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Service
public class CoverageEnumerator {

    /**
     * 
     * @description: 执行连续分页遍历并阻断重复 token、缺页和无限循环
     *
     * @param: fetcher 分页来源端口
     * @param: maxPages 本次遍历允许的最大页数
     * @return: 包含原始证据和初步完整性分类的结果
     * @throws: IllegalArgumentException 最大页数不合法
     * @author: yigeyige99
     */
    public <T> CoverageEnumerationResult<T> enumerate(PageFetcher<T> fetcher, int maxPages) {
        if (fetcher == null || maxPages < 1) {
            throw new IllegalArgumentException("fetcher and maxPages are required");
        }
        List<CoveragePage<T>> pages = new ArrayList<>();
        List<T> records = new ArrayList<>();
        List<String> traversedPaths = new ArrayList<>();
        List<String> uncoveredPaths = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        Set<String> seenTokens = new HashSet<>();
        Long expectedTotal = null;
        String watermark = null;
        String token = null;
        int pageNumber = 1;
        boolean unknown = false;

        while (true) {
            if (pageNumber > maxPages) {
                failures.add("maximum page limit exceeded");
                break;
            }
            CoveragePage<T> page;
            try {
                page = fetcher.fetch(pageNumber, token);
            } catch (Exception exception) {
                failures.add("page " + pageNumber + " fetch failed");
                break;
            }
            pages.add(page);
            traversedPaths.add(page.traversedPath());
            if (page.pageNumber() != pageNumber) {
                uncoveredPaths.add("expected page " + pageNumber + " but received " + page.pageNumber());
                break;
            }
            if (expectedTotal == null) {
                expectedTotal = page.expectedTotal();
            } else if (!expectedTotal.equals(page.expectedTotal())) {
                failures.add("expected total changed");
                unknown = true;
                break;
            }
            if (watermark == null) {
                watermark = page.sourceWatermark();
            } else if (!watermark.equals(page.sourceWatermark())) {
                failures.add("source watermark changed");
                unknown = true;
                break;
            }
            records.addAll(page.records());
            if (!page.hasNext()) {
                break;
            }
            if (!seenTokens.add(page.nextToken())) {
                failures.add("duplicate continuation token");
                unknown = true;
                break;
            }
            token = page.nextToken();
            pageNumber++;
        }

        long observedTotal = records.size();
        CoverageCompleteness completeness = classify(pages, expectedTotal, observedTotal, uncoveredPaths, failures,
                unknown);
        return new CoverageEnumerationResult<>(pages, records, expectedTotal, observedTotal, traversedPaths,
                uncoveredPaths, failures, watermark == null ? "UNKNOWN" : watermark,
                digest(records), completeness);
    }

    private static CoverageCompleteness classify(List<?> pages, Long expectedTotal, long observedTotal,
            List<String> uncoveredPaths, List<String> failures, boolean unknown) {
        if (unknown || pages.isEmpty()) {
            return CoverageCompleteness.UNKNOWN;
        }
        if (!failures.isEmpty() || !uncoveredPaths.isEmpty() || expectedTotal == null
                || expectedTotal.longValue() != observedTotal) {
            return CoverageCompleteness.PARTIAL;
        }
        return CoverageCompleteness.COMPLETE;
    }

    private static String digest(List<?> records) {
        return Sha256Digest.hex(records.stream().map(CoverageEnumerator::canonical).sorted().toList().toString());
    }

    private static String canonical(Object value) {
        if (value instanceof java.util.Map<?, ?> map) {
            return map.entrySet().stream().sorted(java.util.Comparator.comparing(entry -> String.valueOf(entry.getKey())))
                    .map(entry -> String.valueOf(entry.getKey()) + "=" + canonical(entry.getValue())).toList().toString();
        }
        if (value instanceof Iterable<?> iterable) {
            List<String> values = new ArrayList<>();
            iterable.forEach(item -> values.add(canonical(item)));
            return values.toString();
        }
        return String.valueOf(value);
    }
}
