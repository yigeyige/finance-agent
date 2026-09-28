package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 面向业务事件流的最小 Subagent 完成事件。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:40:00
 */
public record SubagentEvent(String eventType, UUID invocationId, UUID taskId, UUID userId,
        String resultDigest, Instant occurredAt) {

    public SubagentEvent {
        if (!"SUBAGENT_COMPLETED".equals(eventType) || invocationId == null || taskId == null || userId == null
                || resultDigest == null || !resultDigest.matches("[0-9a-f]{64}") || occurredAt == null) {
            throw new IllegalArgumentException("subagent event is invalid");
        }
    }
}
