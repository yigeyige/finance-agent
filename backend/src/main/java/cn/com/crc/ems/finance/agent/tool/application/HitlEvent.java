package cn.com.crc.ems.finance.agent.tool.application;

import java.time.Instant;
import java.util.UUID;

/** 脱敏的人在环事件，不承载 prompt、工具参数原文或 permit。 */
public record HitlEvent(UUID requestId, String toolCode, HitlEventType type, String code, Instant occurredAt) {

    public HitlEvent {
        if (requestId == null || toolCode == null || toolCode.isBlank() || type == null
                || code == null || code.isBlank() || occurredAt == null) {
            throw new IllegalArgumentException("HITL event is invalid");
        }
    }
}
