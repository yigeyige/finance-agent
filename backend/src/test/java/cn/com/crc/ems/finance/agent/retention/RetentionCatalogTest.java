package cn.com.crc.ems.finance.agent.retention;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class RetentionCatalogTest {

    @Test
    void catalogCoversEveryRuntimeTableWithoutConfigurationTables() {
        Set<String> names = RetentionCatalog.tables().stream()
                .map(table -> table.schema() + "." + table.name())
                .collect(Collectors.toSet());
        assertThat(names).hasSize(31).contains("task.investigation_task", "task.task_attachment",
                "task.process_artifact_file", "evidence.fact_snapshot", "packet.packet_revision",
                "harness.checkpoint", "governance.audit_event", "governance.outbox_event")
                .doesNotContain("iam.agent_user", "task.business_scene_skill",
                        "task.business_scene_skill_attachment", "knowledge.document",
                        "governance.model_connection_config");
    }

    @Test
    void cutoffIsThirtyFullDaysBeforeFixedClock() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-04T00:00:00Z"), ZoneOffset.UTC);
        assertThat(RetentionCatalog.cutoff(clock)).isEqualTo(Instant.parse("2026-10-05T00:00:00Z"));
    }

    @Test
    void fileRowsUseBatchCreationTime() {
        RetentionCatalog.Table file = RetentionCatalog.tables().stream()
                .filter(table -> table.name().equals("process_artifact_file"))
                .findFirst().orElseThrow();
        assertThat(file.deleteSql()).contains("task.process_artifact_batch", "batch.created_at < ?");
    }

    @Test
    void auditCleanupAlsoRemovesEventsLinkedToExpiredTasks() {
        RetentionCatalog.Table audit = RetentionCatalog.tables().stream()
                .filter(table -> table.fullName().equals("governance.audit_event"))
                .findFirst().orElseThrow();
        assertThat(audit.deleteSql()).contains("task.investigation_task", "task.updated_at < ?");
        assertThat(audit.cutoffParameters()).isEqualTo(2);
    }

    @Test
    void eachBatchLocksOnlyItsEligibleRows() {
        for (RetentionCatalog.Table table : RetentionCatalog.tables()) {
            assertThat(table.deleteSql()).contains("LIMIT ? FOR UPDATE OF target SKIP LOCKED");
        }
    }
}
