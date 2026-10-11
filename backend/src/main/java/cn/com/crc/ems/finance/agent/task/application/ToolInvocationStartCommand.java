package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.ToolInvocationKey;

/**
 *
 * @Description: 请求在外部工具调用前记录幂等意图。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ToolInvocationStartCommand(ToolInvocationKey key, UUID approvalId, String toolCode,
        String requestDigest, UUID actorId, Instant requestedAt) {

    public ToolInvocationStartCommand {
        if (key == null || approvalId == null || actorId == null || requestedAt == null) {
            throw new IllegalArgumentException("invocation key, approval, actor and timestamp are required");
        }
        requireText(toolCode, "toolCode");
        requireDigest(requestDigest, "requestDigest");
    }

    static void requireDigest(String value, String name) {
        if (value == null || !value.matches("^[0-9a-fA-F]{64}$")) {
            throw new IllegalArgumentException(name + " must be a SHA-256 hex digest");
        }
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
    }
}
