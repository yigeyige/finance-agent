package cn.com.crc.ems.finance.agent.iam.web;

import java.util.Set;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 *
 * @Description: 当前身份与前端导航权限响应。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record CurrentIdentityResponse(UUID userId, String authSubject, String displayName, Set<RoleCode> roles,
        Set<IdentityPermission> permissions, boolean testIdentity) {

    /** 将可信身份转换为稳定的前端响应。 */
    public static CurrentIdentityResponse from(CurrentIdentity identity, boolean testIdentity) {
        return new CurrentIdentityResponse(identity.userId(), identity.authSubject(), identity.displayName(),
                identity.roles(), permissions(identity), testIdentity);
    }

    private static Set<IdentityPermission> permissions(CurrentIdentity identity) {
        java.util.EnumSet<IdentityPermission> permissions = java.util.EnumSet.noneOf(IdentityPermission.class);
        if (identity.hasRole(RoleCode.OPERATOR)) {
            permissions.add(IdentityPermission.TASK_READ);
            permissions.add(IdentityPermission.TASK_WRITE);
            permissions.add(IdentityPermission.TASK_EVENTS_READ);
        }
        if (identity.hasRole(RoleCode.VIEWER)) {
            permissions.add(IdentityPermission.PUBLISHED_PACKET_READ);
            permissions.add(IdentityPermission.TASK_EVENTS_READ);
        }
        if (identity.hasRole(RoleCode.PLATFORM_ADMIN)) {
            permissions.add(IdentityPermission.CONFIG_MANAGE);
        }
        return Set.copyOf(permissions);
    }
}
