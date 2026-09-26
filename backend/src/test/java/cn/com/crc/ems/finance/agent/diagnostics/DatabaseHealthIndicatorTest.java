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
                (url, connectionProperties) -> {
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
        properties.getDatabase().setConnectTimeoutSeconds(3);
        properties.getDatabase().setSocketTimeoutSeconds(4);
        properties.getDatabase().setValidationTimeoutSeconds(5);
        Connection connection = mock(Connection.class);
        when(connection.isValid(5)).thenReturn(true);
        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(properties,
                (url, connectionProperties) -> {
                    assertThat(url).isEqualTo("jdbc:postgresql://db/finance");
                    assertThat(connectionProperties)
                            .containsEntry("user", "agent")
                            .containsEntry("password", "test-only")
                            .containsEntry("connectTimeout", "3")
                            .containsEntry("socketTimeout", "4");
                    return connection;
                });

        assertThat(indicator.check().status()).isEqualTo("UP");
    }
}
