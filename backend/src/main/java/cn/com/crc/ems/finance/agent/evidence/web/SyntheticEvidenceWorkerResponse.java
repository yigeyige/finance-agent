package cn.com.crc.ems.finance.agent.evidence.web;

import java.util.UUID;

import cn.com.crc.ems.finance.agent.evidence.application.SyntheticEvidenceWorkerResult;

/**
 *
 * @Description: 本地合成 Worker 的安全 HTTP 响应。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record SyntheticEvidenceWorkerResponse(UUID taskId, UUID stepId, UUID invocationId,
        int snapshotCount, UUID findingId, String resultDigest, boolean replayed) {

    public static SyntheticEvidenceWorkerResponse from(SyntheticEvidenceWorkerResult result) {
        return new SyntheticEvidenceWorkerResponse(result.taskId(), result.stepId(), result.invocationId(),
                result.snapshotCount(), result.findingId(), result.resultDigest(), result.replayed());
    }
}
