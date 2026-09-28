package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/**
 *
 * @Description: 回传主任务状态机的结构化 Subagent 结果，不包含发布或状态变更指令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:40:00
 */
public record SubagentResult(UUID invocationId, UUID taskId, UUID userId, String subagentId,
        String summary, List<String> outputRefs, String resultDigest) {

    public SubagentResult {
        if (invocationId == null || taskId == null || userId == null || subagentId == null || subagentId.isBlank()
                || summary == null || summary.isBlank() || outputRefs == null || outputRefs.isEmpty()
                || resultDigest == null || !resultDigest.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("subagent result is invalid");
        }
        outputRefs = List.copyOf(outputRefs);
        String expected = digest(invocationId, taskId, userId, subagentId, summary, outputRefs);
        if (!expected.equals(resultDigest)) {
            throw new IllegalArgumentException("subagent result digest mismatch");
        }
    }

    public static SubagentResult create(UUID invocationId, UUID taskId, UUID userId, String subagentId,
            String summary, List<String> outputRefs) {
        return new SubagentResult(invocationId, taskId, userId, subagentId, summary, outputRefs,
                digest(invocationId, taskId, userId, subagentId, summary, outputRefs));
    }

    private static String digest(UUID invocationId, UUID taskId, UUID userId, String subagentId,
            String summary, List<String> outputRefs) {
        return Sha256Digest.hex(String.join("|", invocationId.toString(), taskId.toString(), userId.toString(),
                subagentId, summary, String.join(",", outputRefs)));
    }
}
