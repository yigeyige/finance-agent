package cn.com.crc.ems.finance.agent.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ProductionStartupValidatorTest {

    @Test
    void rejectsMissingAndUnsafeProductionConfigurationWithoutSecrets() {
        FinanceAgentProperties properties = new FinanceAgentProperties();
        properties.getDatabase().setPassword("database-secret");
        properties.getDeepSeek().setStub(true);
        properties.getSource().setStub(true);

        assertThatThrownBy(() -> ProductionStartupValidator.validateProduction(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("finance-agent.database.url")
                .hasMessageContaining("finance-agent.deep-seek.stub")
                .hasMessageContaining("finance-agent.security.model-secret-key")
                .hasMessageContaining("finance-agent.identity.mode")
                .hasMessageContaining("finance-agent.storage.mode")
                .hasMessageContaining("finance-agent.source.stub")
                .hasMessageContaining("finance-agent.field-policy.strict")
                .hasMessageNotContaining("database-secret")
                .hasMessageNotContaining("deepseek-secret");
    }

    @Test
    void acceptsCompleteProductionConfiguration() {
        FinanceAgentProperties properties = new FinanceAgentProperties();
        properties.getDatabase().setUrl("jdbc:postgresql://db/finance");
        properties.getDatabase().setUsername("agent");
        properties.getDatabase().setPassword("database-secret");
        properties.getDeepSeek().setStub(false);
        properties.getSecurity().setModelSecretKey("01234567890123456789012345678901");
        properties.getWorkspace().setRoot(Path.of("srv", "finance-agent").toAbsolutePath().toString());
        properties.getIdentity().setMode(FinanceAgentProperties.IdentityMode.SSO);
        properties.getStorage().setMode(FinanceAgentProperties.StorageMode.OBJECT_STORE);
        properties.getStorage().setAdapterId("s3");
        properties.getSource().setStub(false);
        properties.getFieldPolicy().setStrict(true);

        assertThatCode(() -> ProductionStartupValidator.validateProduction(properties)).doesNotThrowAnyException();
    }

    @Test
    void localDefaultsCanKeepStubAndLocalStorage() {
        FinanceAgentProperties properties = new FinanceAgentProperties();
        properties.getDeepSeek().setStub(true);
        properties.getIdentity().setMode(FinanceAgentProperties.IdentityMode.TEST);
        properties.getStorage().setMode(FinanceAgentProperties.StorageMode.LOCAL);

        assertThat(properties.getDeepSeek().isStub()).isTrue();
        assertThat(properties.getIdentity().getMode()).isEqualTo(FinanceAgentProperties.IdentityMode.TEST);
        assertThat(properties.getStorage().getMode()).isEqualTo(FinanceAgentProperties.StorageMode.LOCAL);
    }

    @Test
    void rejectsProductionWithoutFormalStorageAdapter() {
        FinanceAgentProperties properties = new FinanceAgentProperties();
        properties.getDatabase().setUrl("jdbc:postgresql://db/finance");
        properties.getDatabase().setUsername("agent");
        properties.getDatabase().setPassword("database-secret");
        properties.getDeepSeek().setStub(false);
        properties.getSecurity().setModelSecretKey("01234567890123456789012345678901");
        properties.getWorkspace().setRoot(Path.of("srv", "finance-agent").toAbsolutePath().toString());
        properties.getIdentity().setMode(FinanceAgentProperties.IdentityMode.SSO);
        properties.getStorage().setMode(FinanceAgentProperties.StorageMode.OBJECT_STORE);
        properties.getStorage().setAdapterId("none");
        properties.getSource().setStub(false);
        properties.getFieldPolicy().setStrict(true);

        assertThatThrownBy(() -> ProductionStartupValidator.validateProduction(properties))
                .hasMessageContaining("finance-agent.storage.adapter-id")
                .hasMessageNotContaining("database-secret")
                .hasMessageNotContaining("deepseek-secret");
    }
}
