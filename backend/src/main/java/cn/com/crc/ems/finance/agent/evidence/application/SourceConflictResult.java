package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.List;

/**
 *
 * @Description: 来源权威协调后的可审计结果。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record SourceConflictResult(String resolvedSourceId, SourceConflictCode code,
        List<SourceCandidate> candidates) {

    public SourceConflictResult {
        if (code == null || candidates == null) {
            throw new IllegalArgumentException("source conflict result is invalid");
        }
        candidates = List.copyOf(candidates);
    }
}
