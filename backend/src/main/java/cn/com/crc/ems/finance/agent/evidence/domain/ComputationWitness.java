package cn.com.crc.ems.finance.agent.evidence.domain;

import java.time.Instant;
import java.util.UUID;

/** 为确定性判断保存输入、算法和复算结果摘要的计算见证。 */
public record ComputationWitness(UUID witnessId, UUID findingId, String algorithmVersion,
        String ruleSetVersion, String inputDigest, String outputDigest, String snapshotIds,
        Instant computedAt, RecomputeStatus recomputeStatus, Instant recomputedAt, UUID createdBy,
        Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public ComputationWitness {
        if (witnessId == null || findingId == null || computedAt == null || recomputeStatus == null
                || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("witness identity, status and timestamps are required");
        }
        if (blank(algorithmVersion) || blank(ruleSetVersion) || blank(inputDigest)
                || blank(outputDigest) || blank(snapshotIds)) {
            throw new IllegalArgumentException("witness versions, digests and snapshots are invalid");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
