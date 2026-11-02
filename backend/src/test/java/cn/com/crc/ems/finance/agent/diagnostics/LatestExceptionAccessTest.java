package cn.com.crc.ems.finance.agent.diagnostics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.iam.web.PermissionGuardInterceptor;

/**
 * @Description: 验证最近异常入口的控制器和请求拦截器均仅允许平台管理员。
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
    void controllerRejectsOperatorAndAllowsAdministrator() {
        var controller = new LatestExceptionController(new LatestExceptionStore(Clock.systemUTC()));
        IdentityContext.set(identity(RoleCode.OPERATOR));
        assertThatThrownBy(controller::latest).isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));

        IdentityContext.set(identity(RoleCode.PLATFORM_ADMIN));
        assertThat(controller.latest().item()).isNull();
    }

    @Test
    void interceptorRejectsOperatorBeforeController() throws Exception {
        var request = new MockHttpServletRequest("GET", "/api/v1/latest-exception");
        var response = new MockHttpServletResponse();
        IdentityContext.set(identity(RoleCode.OPERATOR));

        assertThat(new PermissionGuardInterceptor().preHandle(request, response, null)).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);

        IdentityContext.set(identity(RoleCode.PLATFORM_ADMIN));
        assertThat(new PermissionGuardInterceptor().preHandle(request, new MockHttpServletResponse(), null)).isTrue();
    }

    private static CurrentIdentity identity(RoleCode role) {
        return new CurrentIdentity(UUID.randomUUID(), "test", "测试用户", Set.of(role));
    }
}
