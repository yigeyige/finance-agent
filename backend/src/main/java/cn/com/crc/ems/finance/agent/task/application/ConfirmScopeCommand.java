package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 追加并确认不可变范围修订的应用命令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ConfirmScopeCommand(UUID taskId, String systemCode, String objectSelector,
        String effectiveTimeRange, String includedPaths, String excludedPaths, String allowedReadFields,
        String allowedRetainFields, String allowedModelFields, String schemaVersion, UUID confirmedBy,
        Instant occurredAt) {

    public ConfirmScopeCommand {
        requireId(taskId, "taskId");
        requireText(systemCode, "systemCode");
        requireText(objectSelector, "objectSelector");
        requireText(effectiveTimeRange, "effectiveTimeRange");
        requireText(includedPaths, "includedPaths");
        requireText(excludedPaths, "excludedPaths");
        requireText(allowedReadFields, "allowedReadFields");
        requireText(allowedRetainFields, "allowedRetainFields");
        requireText(allowedModelFields, "allowedModelFields");
        requireText(schemaVersion, "schemaVersion");
        requireId(confirmedBy, "confirmedBy");
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt is required");
        }
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
    }
}
