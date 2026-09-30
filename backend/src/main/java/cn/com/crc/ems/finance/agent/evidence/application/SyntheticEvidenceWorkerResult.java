package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.UUID;

/** 本地合成证据执行的安全结果摘要。 */
public record SyntheticEvidenceWorkerResult(UUID taskId, UUID stepId, UUID invocationId,
        int snapshotCount, UUID findingId, String resultDigest, boolean replayed) {

    public SyntheticEvidenceWorkerResult {
        if (taskId == null || stepId == null || invocationId == null || snapshotCount < 0
                || resultDigest == null || !resultDigest.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("synthetic execution result is invalid");
        }
    }
}
