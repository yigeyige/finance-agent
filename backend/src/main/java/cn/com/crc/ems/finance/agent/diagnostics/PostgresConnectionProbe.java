package cn.com.crc.ems.finance.agent.diagnostics;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Standalone, read-only PostgreSQL/pgvector connection probe.
 *
 * <p>The probe deliberately does not use Spring Boot configuration defaults. It
 * requires all connection values to be supplied by the process environment and
 * never prints the URL, username, password, or raw JDBC exception message.</p>
 */
public final class PostgresConnectionProbe {

    static final String DB_URL = "FINANCE_AGENT_DB_URL";
    static final String DB_USERNAME = "FINANCE_AGENT_DB_USERNAME";
    static final String DB_PASSWORD = "FINANCE_AGENT_DB_PASSWORD";
    static final String EXPECTED_PG_VERSION = "FINANCE_AGENT_EXPECTED_PG_VERSION";
    static final String EXPECTED_PGVECTOR_VERSION = "FINANCE_AGENT_EXPECTED_PGVECTOR_VERSION";
    static final String DEFAULT_PG_VERSION = "17.11";
    static final String DEFAULT_PGVECTOR_VERSION = "0.8.6";

    private static final Pattern VERSION_PATTERN = Pattern.compile("\\d+(?:\\.\\d+){0,2}");

    private PostgresConnectionProbe() {
    }

    public static void main(String[] args) {
        if (args.length > 0 && "--help".equals(args[0])) {
            printUsage(System.out);
            return;
        }
        if (args.length > 0 && !"--check-config".equals(args[0])) {
            System.out.println("status=CONFIGURATION_ERROR");
            System.out.println("reason=unsupported_argument");
            System.exit(ProbeExitCode.CONFIGURATION_ERROR.code());
        }
        boolean checkOnly = args.length > 0 && "--check-config".equals(args[0]);
        int exitCode = execute(System.getenv(), DriverManager::getConnection, System.out, checkOnly);
        System.exit(exitCode);
    }

    static int execute(Map<String, String> environment, ConnectionOpener opener, PrintStream output,
                       boolean checkOnly) {
        final ProbeConfiguration configuration;
        try {
            configuration = ProbeConfiguration.fromEnvironment(environment);
        } catch (ProbeConfigurationException exception) {
            output.println("status=CONFIGURATION_ERROR");
            output.println("key=" + exception.key());
            return ProbeExitCode.CONFIGURATION_ERROR.code();
        }

        if (checkOnly) {
            output.println("status=CONFIGURATION_OK");
            output.println("expectedPostgresVersion=" + configuration.expectedPostgresVersion());
            output.println("expectedPgvectorVersion=" + configuration.expectedPgvectorVersion());
            return ProbeExitCode.OK.code();
        }

        try (Connection connection = opener.open(
                configuration.url(), configuration.username(), configuration.password())) {
            String postgresVersion = querySingleString(connection, "SHOW server_version");
            if (!postgresVersion.startsWith(configuration.expectedPostgresVersion())) {
                output.println("status=POSTGRES_VERSION_MISMATCH");
                output.println("expected=" + configuration.expectedPostgresVersion());
                output.println("actual=" + postgresVersion);
                return ProbeExitCode.POSTGRES_VERSION_MISMATCH.code();
            }

            String pgvectorVersion;
            try {
                pgvectorVersion = queryNullableString(connection,
                        "SELECT extversion FROM pg_extension WHERE extname = 'vector'");
            } catch (SQLException exception) {
                printSqlFailure(output, "PGVECTOR_QUERY_FAILED", exception);
                return ProbeExitCode.PGVECTOR_UNAVAILABLE.code();
            }
            if (pgvectorVersion == null) {
                output.println("status=PGVECTOR_MISSING");
                output.println("expected=" + configuration.expectedPgvectorVersion());
                output.println("actual=absent");
                return ProbeExitCode.PGVECTOR_UNAVAILABLE.code();
            }
            if (!pgvectorVersion.startsWith(configuration.expectedPgvectorVersion())) {
                output.println("status=PGVECTOR_VERSION_MISMATCH");
                output.println("expected=" + configuration.expectedPgvectorVersion());
                output.println("actual=" + pgvectorVersion);
                return ProbeExitCode.PGVECTOR_VERSION_MISMATCH.code();
            }

            int vectorDimensions;
            try {
                vectorDimensions = queryInt(connection, "SELECT vector_dims('[1,2,3]'::vector)");
            } catch (SQLException exception) {
                printSqlFailure(output, "PGVECTOR_OPERATION_FAILED", exception);
                return ProbeExitCode.PGVECTOR_OPERATION_FAILED.code();
            }
            if (vectorDimensions != 3) {
                output.println("status=PGVECTOR_OPERATION_FAILED");
                output.println("expectedDimensions=3");
                output.println("actualDimensions=" + vectorDimensions);
                return ProbeExitCode.PGVECTOR_OPERATION_FAILED.code();
            }

            output.println("status=OK");
            output.println("postgresVersion=" + postgresVersion);
            output.println("pgvectorVersion=" + pgvectorVersion);
            output.println("vectorDimensions=" + vectorDimensions);
            return ProbeExitCode.OK.code();
        } catch (SQLException exception) {
            printSqlFailure(output, "CONNECTION_FAILED", exception);
            return ProbeExitCode.CONNECTION_FAILED.code();
        }
    }

    private static void printUsage(PrintStream output) {
        output.println("PostgreSQL/pgvector probe");
        output.println("Required: FINANCE_AGENT_DB_URL, FINANCE_AGENT_DB_USERNAME, FINANCE_AGENT_DB_PASSWORD");
        output.println("Optional: FINANCE_AGENT_EXPECTED_PG_VERSION (default 17.11)");
        output.println("Optional: FINANCE_AGENT_EXPECTED_PGVECTOR_VERSION (default 0.8.6)");
        output.println("Use --check-config to validate environment variables without connecting.");
    }

    private static void printSqlFailure(PrintStream output, String status, SQLException exception) {
        output.println("status=" + status);
        String sqlState = exception.getSQLState();
        if (sqlState != null && !sqlState.isBlank()) {
            output.println("sqlState=" + sqlState);
        }
    }

    private static String querySingleString(Connection connection, String sql) throws SQLException {
        String value = queryNullableString(connection, sql);
        if (value == null) {
            throw new SQLException("query returned no rows");
        }
        return value;
    }

    private static String queryNullableString(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next() ? resultSet.getString(1) : null;
        }
    }

    private static int queryInt(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (!resultSet.next()) {
                throw new SQLException("query returned no rows");
            }
            return resultSet.getInt(1);
        }
    }

    @FunctionalInterface
    interface ConnectionOpener {
        Connection open(String url, String username, String password) throws SQLException;
    }

    enum ProbeExitCode {
        OK(0),
        CONFIGURATION_ERROR(2),
        CONNECTION_FAILED(3),
        POSTGRES_VERSION_MISMATCH(4),
        PGVECTOR_UNAVAILABLE(5),
        PGVECTOR_VERSION_MISMATCH(6),
        PGVECTOR_OPERATION_FAILED(7);

        private final int code;

        ProbeExitCode(int code) {
            this.code = code;
        }

        int code() {
            return code;
        }
    }

    record ProbeConfiguration(String url, String username, String password,
                              String expectedPostgresVersion, String expectedPgvectorVersion) {

        static ProbeConfiguration fromEnvironment(Map<String, String> environment) {
            Objects.requireNonNull(environment, "environment");
            String url = required(environment, DB_URL);
            String username = required(environment, DB_USERNAME);
            String password = required(environment, DB_PASSWORD);
            String expectedPg = version(environment.get(EXPECTED_PG_VERSION), DEFAULT_PG_VERSION,
                    EXPECTED_PG_VERSION);
            String expectedPgvector = version(environment.get(EXPECTED_PGVECTOR_VERSION),
                    DEFAULT_PGVECTOR_VERSION, EXPECTED_PGVECTOR_VERSION);
            return new ProbeConfiguration(url, username, password, expectedPg, expectedPgvector);
        }

        private static String required(Map<String, String> environment, String key) {
            String value = environment.get(key);
            if (value == null || value.isBlank()) {
                throw new ProbeConfigurationException(key);
            }
            return value;
        }

        private static String version(String value, String defaultValue, String key) {
            String resolved = value == null || value.isBlank() ? defaultValue : value;
            if (!VERSION_PATTERN.matcher(resolved).matches()) {
                throw new ProbeConfigurationException(key);
            }
            return resolved;
        }
    }

    static final class ProbeConfigurationException extends IllegalArgumentException {
        private final String key;

        ProbeConfigurationException(String key) {
            super(key);
            this.key = key;
        }

        String key() {
            return key;
        }
    }
}
