package cn.com.crc.ems.finance.agent.emsdbconfig.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigService;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsSchemaCatalog;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsSchemaCatalogService;
import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 *
 * @Description: 验证 EMS 实例结构目录详情入口仅向管理员返回本地快照。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 22:20:00
 */
class EmsDatabaseSchemaCatalogControllerTest {
    private final EmsDatabaseConnectionConfigService configService = mock(EmsDatabaseConnectionConfigService.class);
    private final EmsSchemaCatalogService catalogService = mock(EmsSchemaCatalogService.class);
    private final EmsDatabaseConnectionConfigController controller = new EmsDatabaseConnectionConfigController(configService, catalogService);

    @AfterEach
    void clearIdentity() { IdentityContext.clear(); }

    @Test
    void returnsSavedCatalogForAdministrator() {
        UUID id = UUID.randomUUID();
        var catalog = new EmsSchemaCatalog(List.of());
        IdentityContext.set(identity(RoleCode.PLATFORM_ADMIN));
        when(catalogService.view(id)).thenReturn(Optional.of(catalog));

        assertThat(controller.schemaCatalog(id).getBody()).isSameAs(catalog);
    }

    @Test
    void returnsNotFoundWhenInstanceHasNeverBeenSynchronized() {
        UUID id = UUID.randomUUID();
        IdentityContext.set(identity(RoleCode.PLATFORM_ADMIN));
        when(catalogService.view(id)).thenReturn(Optional.empty());

        assertThat(controller.schemaCatalog(id).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void rejectsNonAdministratorBeforeLoadingCatalog() {
        IdentityContext.set(identity(RoleCode.PLATFORM_USER));

        assertThatThrownBy(() -> controller.schemaCatalog(UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
        verifyNoInteractions(catalogService);
    }

    private static CurrentIdentity identity(RoleCode role) {
        return new CurrentIdentity(UUID.randomUUID(), "tester", "测试人员", Set.of(role));
    }
}
