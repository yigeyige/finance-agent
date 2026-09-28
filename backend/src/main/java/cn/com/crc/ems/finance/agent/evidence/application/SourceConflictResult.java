package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.List;

/** 来源权威协调后的可审计结果。 */
public record SourceConflictResult(String resolvedSourceId, SourceConflictCode code,
        List<SourceCandidate> candidates) {

    public SourceConflictResult {
        if (code == null || candidates == null) {
            throw new IllegalArgumentException("source conflict result is invalid");
        }
        candidates = List.copyOf(candidates);
    }
}
