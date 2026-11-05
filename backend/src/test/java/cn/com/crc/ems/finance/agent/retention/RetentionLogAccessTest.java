package cn.com.crc.ems.finance.agent.retention;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.iam.web.PermissionGuardInterceptor;

class RetentionLogAccessTest {
    @TempDir Path root;

    @AfterEach void clear() { IdentityContext.clear(); }

    @Test
    void cleanupLogIsVisibleOnlyToPlatformAdministrator() throws Exception {
        RetentionLogController controller = new RetentionLogController(new RetentionRunLog(root));
        IdentityContext.set(identity(RoleCode.PLATFORM_USER));
        assertThatThrownBy(controller::recent).isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode())
                        .isEqualTo(HttpStatus.FORBIDDEN));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/retention-runs");
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(new PermissionGuardInterceptor().preHandle(request, response, null)).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);

        IdentityContext.set(identity(RoleCode.PLATFORM_ADMIN));
        assertThat(controller.recent().items()).isEmpty();
    }

    private static CurrentIdentity identity(RoleCode role) {
        return new CurrentIdentity(UUID.randomUUID(), "test", "测试用户", Set.of(role));
    }
}
