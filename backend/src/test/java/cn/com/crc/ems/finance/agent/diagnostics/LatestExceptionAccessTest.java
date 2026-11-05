package cn.com.crc.ems.finance.agent.diagnostics;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.iam.web.PermissionGuardInterceptor;

/**
 * @Description: 验证最近异常入口按普通用户与管理员隔离。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-02 23:00:00
 */
class LatestExceptionAccessTest {
    @AfterEach
    void clearIdentity() {
        IdentityContext.clear();
    }

    @Test
    void controllerLimitsOrdinaryUserToOwnFailure() {
        var store = new LatestExceptionStore(Clock.systemUTC());
        var own = identity(RoleCode.PLATFORM_USER);
        var other = identity(RoleCode.PLATFORM_USER);
        store.record(LatestExceptionStore.Source.BUSINESS_API, new IllegalStateException(), own.userId());
        store.record(LatestExceptionStore.Source.BUSINESS_API, new IllegalArgumentException(), other.userId());
        var controller = new LatestExceptionController(store);
        IdentityContext.set(own);
        assertThat(controller.latest().item().exceptionType()).isEqualTo(IllegalStateException.class.getName());
        IdentityContext.set(identity(RoleCode.PLATFORM_ADMIN));
        assertThat(controller.latest().item().exceptionType()).isEqualTo(IllegalArgumentException.class.getName());
    }

    @Test
    void interceptorAllowsAuthenticatedUser() throws Exception {
        var request = new MockHttpServletRequest("GET", "/api/v1/latest-exception");
        var response = new MockHttpServletResponse();
        IdentityContext.set(identity(RoleCode.PLATFORM_USER));

        assertThat(new PermissionGuardInterceptor().preHandle(request, response, null)).isTrue();

        IdentityContext.set(identity(RoleCode.PLATFORM_ADMIN));
        assertThat(new PermissionGuardInterceptor().preHandle(request, new MockHttpServletResponse(), null)).isTrue();
    }

    private static CurrentIdentity identity(RoleCode role) {
        return new CurrentIdentity(UUID.randomUUID(), "test", "测试用户", Set.of(role));
    }
}
