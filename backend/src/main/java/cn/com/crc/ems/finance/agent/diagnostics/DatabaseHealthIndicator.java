package cn.com.crc.ems.finance.agent.diagnostics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;

/** Performs a short, read-only database check without exposing connection details. */
@Component("database")
public final class DatabaseHealthIndicator implements HealthIndicator, DiagnosticProbe {

    private final FinanceAgentProperties properties;
    private final ConnectionOpener connectionOpener;

    public DatabaseHealthIndicator(FinanceAgentProperties properties) {
        this(properties, DriverManager::getConnection);
    }

    DatabaseHealthIndicator(FinanceAgentProperties properties, ConnectionOpener connectionOpener) {
        this.properties = properties;
        this.connectionOpener = connectionOpener;
    }

    @Override
    public String name() {
        return "database";
    }

    @Override
    public DiagnosticComponentStatus check() {
        FinanceAgentProperties.Database database = properties.getDatabase();
        if (database == null || isBlank(database.getUrl()) || isBlank(database.getUsername())
                || isBlank(database.getPassword())) {
            return new DiagnosticComponentStatus(name(), "DOWN", "configuration_missing");
        }
        try (Connection connection = connectionOpener.open(withShortTimeout(database.getUrl()),
                database.getUsername(), database.getPassword())) {
            if (connection.isValid(2)) {
                return new DiagnosticComponentStatus(name(), "UP", "read_only_probe_ok");
            }
            return new DiagnosticComponentStatus(name(), "DOWN", "connection_invalid");
        } catch (SQLException | RuntimeException exception) {
            return new DiagnosticComponentStatus(name(), "DOWN", "connection_failed");
        }
    }

    @Override
    public Health health() {
        DiagnosticComponentStatus status = check();
        Health.Builder builder = "UP".equals(status.status()) ? Health.up() : Health.down();
        return builder.withDetail("component", status.name()).withDetail("reason", status.reason()).build();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String withShortTimeout(String url) {
        if (url.contains("connectTimeout=")) {
            return url;
        }
        return url + (url.contains("?") ? "&" : "?") + "connectTimeout=2&socketTimeout=2";
    }

    @FunctionalInterface
    interface ConnectionOpener {
        Connection open(String url, String username, String password) throws SQLException;
    }
}
