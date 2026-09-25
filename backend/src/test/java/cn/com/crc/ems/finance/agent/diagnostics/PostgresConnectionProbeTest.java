package cn.com.crc.ems.finance.agent.diagnostics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class PostgresConnectionProbeTest {

    @Test
    void missingPasswordFailsWithoutFallback() {
        Map<String, String> environment = new HashMap<>();
        environment.put(PostgresConnectionProbe.DB_URL, "jdbc:postgresql://localhost:5432/finance_agent");
        environment.put(PostgresConnectionProbe.DB_USERNAME, "finance_agent");

        assertThatThrownBy(() -> PostgresConnectionProbe.ProbeConfiguration.fromEnvironment(environment))
                .isInstanceOfSatisfying(PostgresConnectionProbe.ProbeConfigurationException.class,
                        exception -> assertThat(exception.key()).isEqualTo(PostgresConnectionProbe.DB_PASSWORD));
    }

    @Test
    void blankPasswordFailsWithoutFallback() {
        Map<String, String> environment = requiredEnvironment();
        environment.put(PostgresConnectionProbe.DB_PASSWORD, "  ");

        assertThatThrownBy(() -> PostgresConnectionProbe.ProbeConfiguration.fromEnvironment(environment))
                .isInstanceOfSatisfying(PostgresConnectionProbe.ProbeConfigurationException.class,
                        exception -> assertThat(exception.key()).isEqualTo(PostgresConnectionProbe.DB_PASSWORD));
    }

    @Test
    void defaultsExpectedVersionsToP0Baseline() {
        PostgresConnectionProbe.ProbeConfiguration configuration =
                PostgresConnectionProbe.ProbeConfiguration.fromEnvironment(requiredEnvironment());

        assertThat(configuration.expectedPostgresVersion()).isEqualTo("17.11");
        assertThat(configuration.expectedPgvectorVersion()).isEqualTo("0.8.6");
    }

    @Test
    void acceptsExplicitExpectedVersions() {
        Map<String, String> environment = requiredEnvironment();
        environment.put(PostgresConnectionProbe.EXPECTED_PG_VERSION, "17");
        environment.put(PostgresConnectionProbe.EXPECTED_PGVECTOR_VERSION, "0.8.6");

        PostgresConnectionProbe.ProbeConfiguration configuration =
                PostgresConnectionProbe.ProbeConfiguration.fromEnvironment(environment);

        assertThat(configuration.expectedPostgresVersion()).isEqualTo("17");
        assertThat(configuration.expectedPgvectorVersion()).isEqualTo("0.8.6");
    }

    @Test
    void invalidExpectedVersionFailsWithEnvironmentKeyOnly() {
        Map<String, String> environment = requiredEnvironment();
        environment.put(PostgresConnectionProbe.EXPECTED_PG_VERSION, "latest");

        assertThatThrownBy(() -> PostgresConnectionProbe.ProbeConfiguration.fromEnvironment(environment))
                .isInstanceOfSatisfying(PostgresConnectionProbe.ProbeConfigurationException.class,
                        exception -> assertThat(exception.key()).isEqualTo(PostgresConnectionProbe.EXPECTED_PG_VERSION));
    }

    private static Map<String, String> requiredEnvironment() {
        Map<String, String> environment = new HashMap<>();
        environment.put(PostgresConnectionProbe.DB_URL, "jdbc:postgresql://localhost:5432/finance_agent");
        environment.put(PostgresConnectionProbe.DB_USERNAME, "finance_agent");
        environment.put(PostgresConnectionProbe.DB_PASSWORD, "test-only-placeholder");
        return environment;
    }
}
