package cn.com.crc.ems.finance.agent.packet;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class PacketSchemaContractTest {

    @Test
    void packetMigrationContainsVersionChainAndImmutabilityConstraints() throws IOException {
        String packet = read("db/migration/V5__packet_and_governance.sql");
        assertThat(packet).contains("CREATE TABLE packet.packet_draft", "canonical_content jsonb NOT NULL",
                "content_digest char(64) NOT NULL", "UNIQUE(task_id, draft_no)",
                "CREATE TABLE packet.packet_review", "scope_revision_id uuid NOT NULL",
                "runtime_bundle_id uuid NOT NULL", "CREATE TABLE packet.packet_revision",
                "html_snapshot text NOT NULL", "supersedes_revision_id uuid",
                "UNIQUE(task_id, revision_no)", "CREATE TABLE packet.handoff",
                "packet_revision_id uuid NOT NULL");
    }

    @Test
    void auditMigrationCoversAllPacketTables() throws IOException {
        String audit = read("db/migration/V8__audit_fields.sql");
        assertThat(audit).contains("ALTER TABLE packet.packet_draft", "ALTER TABLE packet.packet_review",
                "ALTER TABLE packet.packet_revision", "ALTER TABLE packet.handoff")
                .contains("ADD COLUMN IF NOT EXISTS created_by", "ADD COLUMN IF NOT EXISTS updated_at");
    }

    @Test
    void scopeRecheckMigrationPersistsPublicationGateEvidence() throws IOException {
        String recheck = read("db/migration/V11__scope_recheck.sql");
        assertThat(recheck).contains("CREATE TABLE packet.scope_recheck", "decision varchar(30)",
                "result_digest char(64) NOT NULL", "diff_summary text NOT NULL", "created_by uuid",
                "updated_at timestamptz", "ix_scope_recheck_task_checked_at");
    }

    private static String read(String resource) throws IOException {
        try (InputStream stream = PacketSchemaContractTest.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new AssertionError("Missing migration resource: " + resource);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
