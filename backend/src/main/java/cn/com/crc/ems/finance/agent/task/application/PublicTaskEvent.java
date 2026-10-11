package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @Description: 对工作台公开的脱敏任务事件，不承载 prompt、推理链或源系统原文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PublicTaskEvent(UUID taskId, UUID userId, long sequence, String eventType,
        String status, String publicReason, String resultReference, Instant occurredAt) {

    private static final Set<String> TYPES = Set.of("TASK_STATUS_CHANGED", "APPROVAL_REQUIRED", "TOOL_PROGRESS",
            "PUBLIC_REASON", "RESULT_REFERENCE", "TASK_PUBLISHED");
    private static final Set<String> FORBIDDEN = Set.of("prompt", "reasoning", "reasoning_content", "token",
            "credential", "password", "secret", "source_raw");

    public PublicTaskEvent {
        if (taskId == null || userId == null || sequence < 1 || occurredAt == null
                || eventType == null || !TYPES.contains(eventType)) {
            throw new IllegalArgumentException("public task event identity or type is invalid");
        }
        if (containsForbidden(publicReason) || containsForbidden(resultReference)) {
            throw new IllegalArgumentException("public task event contains forbidden content");
        }
    }

    private static boolean containsForbidden(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.toLowerCase(java.util.Locale.ROOT);
        return FORBIDDEN.stream().anyMatch(normalized::contains);
    }
}
