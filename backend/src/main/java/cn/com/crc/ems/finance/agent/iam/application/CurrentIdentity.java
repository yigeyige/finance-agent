package cn.com.crc.ems.finance.agent.iam.application;

import java.util.Set;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/** 当前请求经过身份解析后的最小可信身份。 */
public record CurrentIdentity(UUID userId, String authSubject, String displayName, Set<RoleCode> roles) {

    public CurrentIdentity {
        if (userId == null || authSubject == null || authSubject.isBlank() || displayName == null
                || displayName.isBlank() || roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("current identity is invalid");
        }
        roles = Set.copyOf(roles);
    }

    /** 判断当前身份是否拥有指定角色。 */
    public boolean hasRole(RoleCode role) {
        return roles.contains(role);
    }
}
