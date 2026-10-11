package cn.com.crc.ems.finance.agent.iam.application;

import java.util.Set;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 *
 * @Description: 当前请求经过身份解析后的最小可信身份。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record CurrentIdentity(UUID userId, String authSubject, String displayName, Set<RoleCode> roles) {

    public CurrentIdentity {
        if (userId == null || authSubject == null || authSubject.isBlank() || displayName == null
                || displayName.isBlank() || roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("current identity is invalid");
        }
        roles = Set.copyOf(roles);
    }

    /**
     *
     * @description: 判断当前身份是否拥有指定角色，平台管理员继承工作台内全部业务角色能力。
     *
     * @param: role 待判断角色
     * @return: 是否具备该角色或管理员继承权限
     * @author: yigeyige99
     */
    public boolean hasRole(RoleCode role) {
        return roles.contains(role) || roles.contains(RoleCode.PLATFORM_ADMIN);
    }
}
