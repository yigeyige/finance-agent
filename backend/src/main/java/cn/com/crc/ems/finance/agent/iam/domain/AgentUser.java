package cn.com.crc.ems.finance.agent.iam.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @Description: 表示可审计的 Agent 操作身份。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record AgentUser(
        UUID userId,
        String authSubject,
        String employeeNo,
        String displayName,
        AgentUserStatus status,
        Instant createdAt,
        UUID createdBy,
        Instant updatedAt,
        UUID updatedBy) {

    public AgentUser {
        Objects.requireNonNull(userId, "userId");
        requireText(authSubject, "authSubject");
        requireText(employeeNo, "employeeNo");
        requireText(displayName, "displayName");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(updatedAt, "updatedAt");
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
