package cn.com.crc.ems.finance.agent.modelconfig.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;
import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.modelconfig.domain.ModelConnectionConfig;
import cn.com.crc.ems.finance.agent.modelconfig.web.ModelRuntimeSummaryController;

/**
 *
 * @Description: 运行模型摘要的模式投影、动态配置和身份隔离测试，不连接数据库或调用模型。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-29 01:00:00
 */
class ModelRuntimeSummaryTest {
    private final FinanceAgentProperties properties = new FinanceAgentProperties();
    private final ModelConnectionConfigRepository repository = mock(ModelConnectionConfigRepository.class);
    private final ModelRuntimeSummaryService service = new ModelRuntimeSummaryService(properties, repository);

    @AfterEach
    void clearsIdentity() { IdentityContext.clear(); }

    @Test
    void stubModeDoesNotExposeConfiguredLiveModel() {
        properties.getDeepSeek().setStub(true);
        assertThat(service.current()).isEqualTo(new ModelRuntimeSummary("STUB", "本地模拟模型"));
    }

    @Test
    void liveModeReadsCurrentEnabledModelAndSupportsNoEnabledModel() {
        properties.getDeepSeek().setStub(false);
        UUID operator = UUID.randomUUID();
        Instant now = Instant.parse("2026-10-29T00:00:00Z");
        var config = new ModelConnectionConfig(UUID.randomUUID(), "测试连接", "DEEPSEEK", "https://example.test/private", "test-model",
                null, "synthetic-ciphertext", true, operator, now, operator, now);
        when(repository.findEnabled()).thenReturn(Optional.of(config), Optional.empty());
        assertThat(service.current()).isEqualTo(new ModelRuntimeSummary("LIVE", "test-model"));
        assertThat(service.current()).isEqualTo(new ModelRuntimeSummary("LIVE", null));
    }

    @Test
    void requiresIdentityAndRejectsViewer() {
        var controller = new ModelRuntimeSummaryController(service);
        assertThatThrownBy(controller::current).isInstanceOf(IdentityContext.IdentityRequiredException.class);
        IdentityContext.set(identity(RoleCode.VIEWER));
        assertThatThrownBy(controller::current).isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode().value()).isEqualTo(403));
    }

    @Test
    void permitsOperatorAndInheritedAdministrator() {
        properties.getDeepSeek().setStub(true);
        var controller = new ModelRuntimeSummaryController(service);
        for (RoleCode role : Set.of(RoleCode.OPERATOR, RoleCode.PLATFORM_ADMIN)) {
            IdentityContext.set(identity(role));
            assertThat(controller.current().modelName()).isEqualTo("本地模拟模型");
        }
    }

    /** 创建不含真实用户数据的可信测试身份。 */
    private static CurrentIdentity identity(RoleCode role) {
        return new CurrentIdentity(UUID.randomUUID(), "test-subject", "测试用户", Set.of(role));
    }
}
