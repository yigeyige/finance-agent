package cn.com.crc.ems.finance.agent.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.mock.env.MockEnvironment;

class FinanceAgentPropertiesBindingTest {

    @Test
    void bindsNestedConfigurationWithTypedBudgetAndPolicy() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("finance-agent.database.url", "jdbc:postgresql://db/finance")
                .withProperty("finance-agent.database.username", "agent")
                .withProperty("finance-agent.database.password", "not-printed")
                .withProperty("finance-agent.deep-seek.api-key", "api-key-not-printed")
                .withProperty("finance-agent.deep-seek.base-url", "https://api.deepseek.com")
                .withProperty("finance-agent.deep-seek.model", "deepseek-chat")
                .withProperty("finance-agent.deep-seek.stub", "false")
                .withProperty("finance-agent.source.stub", "true")
                .withProperty("finance-agent.workspace.root", "./runtime")
                .withProperty("finance-agent.budget.max-steps", "32")
                .withProperty("finance-agent.budget.max-tokens", "12000")
                .withProperty("finance-agent.budget.max-cost", "12.50")
                .withProperty("finance-agent.identity.mode", "sso")
                .withProperty("finance-agent.storage.mode", "object-store")
                .withProperty("finance-agent.field-policy.strict", "true");

        FinanceAgentProperties properties = Binder.get(environment)
                .bind("finance-agent", Bindable.of(FinanceAgentProperties.class))
                .orElseThrow(() -> new AssertionError("finance-agent configuration did not bind"));

        assertThat(properties.getBudget().getMaxSteps()).isEqualTo(32);
        assertThat(properties.getBudget().getMaxTokens()).isEqualTo(12000);
        assertThat(properties.getBudget().getMaxCost()).isEqualByComparingTo(new BigDecimal("12.50"));
        assertThat(properties.getDeepSeek().isStub()).isFalse();
        assertThat(properties.getSource().isStub()).isTrue();
        assertThat(properties.getFieldPolicy().isStrict()).isTrue();
    }
}
