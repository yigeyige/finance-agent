package cn.com.crc.ems.finance.agent.evidence;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class EvidenceSchemaContractTest {

    @Test
    void evidenceMigrationContainsAllTablesAndControlledValues() throws IOException {
        String sql = new ClassPathResource("db/migration/V4__evidence.sql")
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(sql).contains("CREATE TABLE evidence.fact_snapshot")
                .contains("CREATE TABLE evidence.coverage_record")
                .contains("CREATE TABLE evidence.finding")
                .contains("CREATE TABLE evidence.finding_evidence")
                .contains("CREATE TABLE evidence.computation_witness")
                .contains("CREATE TABLE evidence.professional_confirmation")
                .contains("CHECK (effective_class IN ('CURRENT_EFFECTIVE','FUTURE_SCHEDULED','UNDETERMINED'))")
                .contains("CHECK (completeness IN ('COMPLETE','PARTIAL','UNKNOWN'))")
                .contains("CHECK (judgment_mode IN ('DETERMINISTIC','HUMAN_CONFIRMED','UNKNOWN'))")
                .contains("UNIQUE REFERENCES evidence.finding(finding_id)");
    }

    @Test
    void auditMigrationCoversEvidenceTables() throws IOException {
        String sql = new ClassPathResource("db/migration/V8__audit_fields.sql")
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(sql).contains("ALTER TABLE evidence.fact_snapshot")
                .contains("ALTER TABLE evidence.coverage_record")
                .contains("ALTER TABLE evidence.finding")
                .contains("ALTER TABLE evidence.finding_evidence")
                .contains("ALTER TABLE evidence.computation_witness")
                .contains("ALTER TABLE evidence.professional_confirmation");
    }
}
