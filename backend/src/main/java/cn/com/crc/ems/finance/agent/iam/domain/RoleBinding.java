package cn.com.crc.ems.finance.agent.iam.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** 表示身份与角色之间可撤销且可审计的授权关系。 */
public record RoleBinding(
        UUID userId,
        RoleCode roleCode,
        UUID grantedBy,
        Instant grantedAt,
        Instant revokedAt,
        UUID createdBy,
        Instant createdAt,
        UUID updatedBy,
        Instant updatedAt) {

    public RoleBinding {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(roleCode, "roleCode");
        Objects.requireNonNull(grantedAt, "grantedAt");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(updatedAt, "updatedAt");
    }
}
