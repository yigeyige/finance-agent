package cn.com.crc.ems.finance.agent.modelconfig.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.modelconfig.domain.ModelConnectionConfig;
import cn.com.crc.ems.finance.agent.modelconfig.web.ModelConnectionConfigResponse;

/** 验证大模型连接配置的密钥保护、更新和唯一启用边界。 */
class ModelConnectionConfigServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-09T13:30:00Z");
    private static final UUID OPERATOR = UUID.randomUUID();

    @Test
    void createsConfigWithEncryptedKeyAndSafeResponse() {
        ModelConnectionConfigRepository repository = mock(ModelConnectionConfigRepository.class);
        ModelApiKeyCipher cipher = mock(ModelApiKeyCipher.class);
        when(cipher.encrypt("secret-key")).thenReturn("ciphertext");
        ModelConnectionConfigService service = new ModelConnectionConfigService(repository, cipher, clock());

        ModelConnectionConfig config = service.create("默认模型", "DEEPSEEK", "https://api.example.test", "deepseek-chat",
                "high", "secret-key", OPERATOR);

        assertThat(config.apiKeyCiphertext()).isEqualTo("ciphertext");
        assertThat(ModelConnectionConfigResponse.from(config).apiKeyConfigured()).isTrue();
        assertThat(ModelConnectionConfigResponse.from(config).toString()).doesNotContain("secret-key", "ciphertext");
        verify(repository).insert(config);
    }

    @Test
    void retainsExistingKeyWhenUpdateDoesNotProvideNewOne() {
        ModelConnectionConfigRepository repository = mock(ModelConnectionConfigRepository.class);
        ModelApiKeyCipher cipher = mock(ModelApiKeyCipher.class);
        UUID configId = UUID.randomUUID();
        ModelConnectionConfig existing = config(configId, "old-ciphertext", true);
        when(repository.findById(configId)).thenReturn(Optional.of(existing));
        ModelConnectionConfigService service = new ModelConnectionConfigService(repository, cipher, clock());

        ModelConnectionConfig updated = service.update(configId, "新名称", "DEEPSEEK", "https://api.example.test",
                "deepseek-reasoner", null, "", OPERATOR);

        assertThat(updated.apiKeyCiphertext()).isEqualTo("old-ciphertext");
        assertThat(updated.name()).isEqualTo("新名称");
        verify(repository).update(updated);
    }

    @Test
    void enablesOnlyRequestedConfig() {
        ModelConnectionConfigRepository repository = mock(ModelConnectionConfigRepository.class);
        UUID configId = UUID.randomUUID();
        ModelConnectionConfig existing = config(configId, "ciphertext", false);
        when(repository.findById(configId)).thenReturn(Optional.of(existing));
        ModelConnectionConfigService service = new ModelConnectionConfigService(repository, mock(ModelApiKeyCipher.class), clock());

        service.enable(configId, OPERATOR);

        verify(repository).disableAll(OPERATOR, NOW);
        verify(repository).update(any(ModelConnectionConfig.class));
    }

    @Test
    void rejectsMissingEnabledConfig() {
        ModelConnectionConfigRepository repository = mock(ModelConnectionConfigRepository.class);
        when(repository.findEnabled()).thenReturn(Optional.empty());
        ModelConnectionConfigService service = new ModelConnectionConfigService(repository, mock(ModelApiKeyCipher.class), clock());

        assertThatThrownBy(service::requireEnabled).isInstanceOf(IllegalStateException.class)
                .hasMessage("请先在大模型配置中启用一个模型");
    }

    private static ModelConnectionConfig config(UUID configId, String ciphertext, boolean enabled) {
        return new ModelConnectionConfig(configId, "默认模型", "DEEPSEEK", "https://api.example.test", "deepseek-chat",
                null, ciphertext, enabled, OPERATOR, NOW.minusSeconds(60), OPERATOR, NOW.minusSeconds(30));
    }

    private static Clock clock() {
        return Clock.fixed(NOW, ZoneOffset.UTC);
    }
}
