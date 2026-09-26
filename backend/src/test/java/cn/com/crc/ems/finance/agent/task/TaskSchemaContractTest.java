package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class TaskSchemaContractTest {

    @Test
    void migrationContainsTaskConcurrencyAndForeignKeyGuards() throws IOException {
        String sql = new ClassPathResource("db/migration/V3__task.sql")
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(sql).contains("row_version bigint NOT NULL DEFAULT 0")
                .contains("CHECK (jsonb_typeof(object_selector) = 'object')")
                .contains("UNIQUE(step_id, revision_no)")
                .contains("ux_one_active_lease_per_task")
                .contains("WHERE released_at IS NULL")
                .contains("idempotency_key varchar(300) NOT NULL UNIQUE")
                .contains("REFERENCES task.scope_revision(scope_revision_id)");
    }
}
