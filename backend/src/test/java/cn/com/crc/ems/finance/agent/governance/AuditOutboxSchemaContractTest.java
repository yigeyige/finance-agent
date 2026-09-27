package cn.com.crc.ems.finance.agent.governance;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/** 验证审计/outbox 增量迁移包含幂等、摘要和不可变保护。 */
class AuditOutboxSchemaContractTest {

    @Test
    void v10StrengthensAuditAndOutboxContracts() throws IOException {
        String sql = read("db/migration/V10__audit_outbox_contract.sql");

        assertThat(sql).contains("canonicalization_version", "idempotency_key", "UNIQUE (idempotency_key)",
                "ix_outbox_pending_delivery", "reject_audit_mutation", "trg_audit_event_immutable",
                "delivery_attempts >= 0");
        assertThat(sql).doesNotContainIgnoringCase("CREATE EXTENSION", "CREATE ROLE", "PASSWORD=");
    }

    private static String read(String resource) throws IOException {
        try (InputStream stream = AuditOutboxSchemaContractTest.class.getClassLoader()
                .getResourceAsStream(resource)) {
            if (stream == null) {
                throw new AssertionError("Missing migration resource: " + resource);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
