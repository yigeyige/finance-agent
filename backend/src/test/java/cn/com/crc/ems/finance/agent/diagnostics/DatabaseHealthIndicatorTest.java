package cn.com.crc.ems.finance.agent.diagnostics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;

class DatabaseHealthIndicatorTest {

    @Test
    void reportsMissingConfigurationWithoutOpeningConnection() {
        FinanceAgentProperties properties = new FinanceAgentProperties();
        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(properties,
                (url, username, password) -> {
                    throw new AssertionError("connection must not be attempted");
                });

        assertThat(indicator.check()).isEqualTo(
                new DiagnosticComponentStatus("database", "DOWN", "configuration_missing"));
    }

    @Test
    void reportsReadOnlyProbeSuccess() throws Exception {
        FinanceAgentProperties properties = new FinanceAgentProperties();
        properties.getDatabase().setUrl("jdbc:postgresql://db/finance");
        properties.getDatabase().setUsername("agent");
        properties.getDatabase().setPassword("test-only");
        Connection connection = mock(Connection.class);
        when(connection.isValid(2)).thenReturn(true);
        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(properties,
                (url, username, password) -> connection);

        assertThat(indicator.check().status()).isEqualTo("UP");
    }
}
