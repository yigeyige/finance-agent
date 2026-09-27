package cn.com.crc.ems.finance.agent.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

class FinanceAgentPropertiesValidationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(PropertiesConfiguration.class);

    @Test
    void rejectsNonPositiveDiagnosticTimeoutDuringBinding() {
        contextRunner.withPropertyValues(
                        "finance-agent.budget.max-steps=1",
                        "finance-agent.budget.max-tokens=1",
                        "finance-agent.database.connect-timeout-seconds=0")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining("connectTimeoutSeconds");
                });
    }

    @Test
    void rejectsHeartbeatThatCanExpireBeforeItRuns() {
        contextRunner.withPropertyValues(
                        "finance-agent.budget.max-steps=1",
                        "finance-agent.budget.max-tokens=1",
                        "finance-agent.worker.lease-duration-millis=1000",
                        "finance-agent.worker.heartbeat-interval-millis=1000")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasStackTraceContaining("heartbeatBeforeLeaseExpiry");
                });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(FinanceAgentProperties.class)
    static class PropertiesConfiguration {
    }
}
