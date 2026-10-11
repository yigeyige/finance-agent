package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 关键业务动作的最小审计定位信息。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record AuditedActionCommand(UUID taskId, UUID actorUserId, String eventType,
        String targetType, String targetId, Instant occurredAt) {

    public AuditedActionCommand {
        if (taskId == null || eventType == null || eventType.isBlank() || targetType == null
                || targetType.isBlank() || targetId == null || targetId.isBlank() || occurredAt == null) {
            throw new IllegalArgumentException("audited action identity is invalid");
        }
    }
}
