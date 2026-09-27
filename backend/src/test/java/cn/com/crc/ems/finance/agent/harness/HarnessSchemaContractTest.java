package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class HarnessSchemaContractTest {

    @Test
    void v6DefinesSessionAndCheckpointAndV9AddsIsolationAndReferences() throws IOException {
        String v6 = read("db/migration/V6__harness.sql");
        String v9 = read("db/migration/V9__harness_plan_workspace.sql");
        assertThat(v6).contains("CREATE TABLE harness.session_state", "task_id uuid NOT NULL",
                "user_id uuid NOT NULL", "CREATE TABLE harness.checkpoint", "state_ref jsonb NOT NULL",
                "state_digest char(64) NOT NULL");
        assertThat(v9).contains("ADD COLUMN IF NOT EXISTS user_id", "scope_revision_id",
                "state_schema_version", "CREATE TABLE harness.plan_revision", "UNIQUE(session_id, revision_no)",
                "CREATE TABLE harness.workspace_ref", "relative_path varchar(1000) NOT NULL",
                "UNIQUE(session_id, relative_path)");
    }

    @Test
    void v9StoresAuditFieldsAndDoesNotStoreRawWorkspaceContent() throws IOException {
        String v9 = read("db/migration/V9__harness_plan_workspace.sql");
        assertThat(v9).contains("plan_digest char(64) NOT NULL", "content_digest char(64) NOT NULL",
                "created_by uuid", "created_at timestamptz", "updated_by uuid", "updated_at timestamptz")
                .doesNotContain("raw_content", "source_raw_response", "reasoning_content");
    }

    private static String read(String resource) throws IOException {
        try (InputStream stream = HarnessSchemaContractTest.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new AssertionError("Missing migration resource: " + resource);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
