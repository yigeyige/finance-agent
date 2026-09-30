package cn.com.crc.ems.finance.agent.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.junit.jupiter.api.Test;

class FlywayMigrationContractTest {

    @Test
    void migrationFilesContainRequiredSchemasAndNoPlatformDdl() throws IOException {
        String sql = List.of(
                        "V1__create_schemas.sql",
                        "V2__iam_and_governance.sql",
                        "V3__task.sql",
                        "V4__evidence.sql",
                        "V5__packet_and_governance.sql",
                        "V6__harness.sql",
                        "V7__knowledge.sql",
                        "V8__audit_fields.sql",
                        "V9__harness_plan_workspace.sql",
                        "V10__audit_outbox_contract.sql",
                        "V11__scope_recheck.sql",
                        "V12__checkpoint_runtime_binding.sql")
                .stream()
                .map(file -> read("db/migration/" + file))
                .reduce("", (left, right) -> left + "\n" + right);

        assertThat(sql).contains("iam.", "task.", "evidence.", "packet.", "harness.", "knowledge.", "governance.");
        assertThat(sql).contains("vector(3)", "ux_one_active_lease_per_task", "created_by", "created_at",
                "updated_by", "updated_at");
        assertThat(sql).doesNotContainIgnoringCase("CREATE ROLE", "CREATE DATABASE", "CREATE EXTENSION",
                "DROP SCHEMA", "DROP DATABASE", "PASSWORD=");
        assertThat(FlywayMigrationContractTest.class.getClassLoader()
                .getResource("db/migration/V0_create_database.sql")).isNull();
        assertThat(FlywayMigrationContractTest.class.getClassLoader()
                .getResource("db/migration/V10__checkpoint_runtime_binding.sql")).isNull();
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
