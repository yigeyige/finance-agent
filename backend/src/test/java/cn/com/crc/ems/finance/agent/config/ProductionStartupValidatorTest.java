package cn.com.crc.ems.finance.agent.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ProductionStartupValidatorTest {

    @Test
    void rejectsMissingAndUnsafeProductionConfigurationWithoutSecrets() {
        FinanceAgentProperties properties = new FinanceAgentProperties();
        properties.getDatabase().setPassword("database-secret");
        properties.getDeepSeek().setApiKey("deepseek-secret");
        properties.getDeepSeek().setStub(true);

        assertThatThrownBy(() -> ProductionStartupValidator.validateProduction(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("finance-agent.database.url")
                .hasMessageContaining("finance-agent.deep-seek.base-url")
                .hasMessageContaining("finance-agent.deep-seek.stub")
                .hasMessageContaining("finance-agent.identity.mode")
                .hasMessageContaining("finance-agent.storage.mode")
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
        properties.getDeepSeek().setApiKey("deepseek-secret");
        properties.getDeepSeek().setBaseUrl("https://api.deepseek.com");
        properties.getDeepSeek().setStub(false);
        properties.getWorkspace().setRoot("/srv/finance-agent");
        properties.getIdentity().setMode("sso");
        properties.getStorage().setMode("object-store");
        properties.getFieldPolicy().setStrict(true);

        assertThatCode(() -> ProductionStartupValidator.validateProduction(properties)).doesNotThrowAnyException();
    }

    @Test
    void localDefaultsCanKeepStubAndLocalStorage() {
        FinanceAgentProperties properties = new FinanceAgentProperties();
        properties.getDeepSeek().setStub(true);
        properties.getIdentity().setMode("test");
        properties.getStorage().setMode("local");

        assertThat(properties.getDeepSeek().isStub()).isTrue();
        assertThat(properties.getIdentity().getMode()).isEqualTo("test");
        assertThat(properties.getStorage().getMode()).isEqualTo("local");
    }
}
