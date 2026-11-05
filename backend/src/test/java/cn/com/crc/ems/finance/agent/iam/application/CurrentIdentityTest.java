package cn.com.crc.ems.finance.agent.iam.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/** 平台管理员角色继承工作台业务权限的回归测试。 */
class CurrentIdentityTest {

    @Test
    void platformAdminInheritsPlatformUserCapabilities() {
        CurrentIdentity identity = new CurrentIdentity(UUID.randomUUID(), "local-admin", "本地平台管理员",
                Set.of(RoleCode.PLATFORM_ADMIN));

        assertThat(identity.hasRole(RoleCode.PLATFORM_ADMIN)).isTrue();
        assertThat(identity.hasRole(RoleCode.PLATFORM_USER)).isTrue();
        CurrentIdentity user = new CurrentIdentity(UUID.randomUUID(), "user", "普通用户", Set.of(RoleCode.PLATFORM_USER));
        assertThat(user.hasRole(RoleCode.PLATFORM_ADMIN)).isFalse();
    }
}
