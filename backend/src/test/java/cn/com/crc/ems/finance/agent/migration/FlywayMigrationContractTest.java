package cn.com.crc.ems.finance.agent.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class FlywayMigrationContractTest {

    @Test
    void migrationFilesContainRequiredSchemasAndNoPlatformDdl() throws IOException {
        String sql = IntStream.rangeClosed(1, 7)
                .mapToObj(version -> read("db/migration/V" + version + "__" + suffix(version) + ".sql"))
                .reduce("", (left, right) -> left + "\n" + right);

        assertThat(sql).contains("iam.", "task.", "evidence.", "packet.", "harness.", "knowledge.", "governance.");
        assertThat(sql).contains("vector(3)", "ux_one_active_lease_per_task");
        assertThat(sql).doesNotContainIgnoringCase("CREATE ROLE", "CREATE DATABASE", "CREATE EXTENSION",
                "DROP SCHEMA", "DROP DATABASE", "PASSWORD=");
    }

    @Test
    void missingPasswordIsRejectedWithoutFallback() {
        Map<String, String> environment = new HashMap<>();
        environment.put(FlywayMigrationRunner.DB_URL, "jdbc:postgresql://localhost:5432/finance_agent");
        environment.put(FlywayMigrationRunner.DB_USERNAME, "fa_migration");

        assertThatThrownBy(() -> FlywayMigrationRunner.MigrationConfiguration.fromEnvironment(environment))
                .isInstanceOfSatisfying(FlywayMigrationRunner.MigrationConfigurationException.class,
                        exception -> assertThat(exception.key()).isEqualTo(FlywayMigrationRunner.DB_PASSWORD));
    }

    private static String suffix(int version) {
        return switch (version) {
            case 1 -> "create_schemas";
            case 2 -> "iam_and_governance";
            case 3 -> "task";
            case 4 -> "evidence";
            case 5 -> "packet_and_governance";
            case 6 -> "harness";
            case 7 -> "knowledge";
            default -> throw new IllegalArgumentException("unsupported migration version");
        };
    }

    private static String read(String resource) {
        try (InputStream stream = FlywayMigrationContractTest.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new AssertionError("Missing migration resource: " + resource);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new AssertionError("Unable to read migration resource: " + resource, exception);
        }
    }
}
