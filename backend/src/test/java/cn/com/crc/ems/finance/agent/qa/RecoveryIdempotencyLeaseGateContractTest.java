package cn.com.crc.ems.finance.agent.qa;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.harness.application.CheckpointRecoveryDecision;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;

/** 校验恢复、幂等和租约故障矩阵与现有服务契约保持一致。 */
class RecoveryIdempotencyLeaseGateContractTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final Instant NOW = Instant.parse("2026-09-29T00:00:00Z");

    @Test
    void fixtureCoversSixFaultPointsAndSafetyInvariants() throws IOException {
        JsonNode fixture = fixture();
        assertThat(fixture.path("fixtureVersion").asText()).isEqualTo("recovery-idempotency-lease-v1");
        assertThat(fixture.path("faultPoints")).hasSize(6);
        assertThat(fixture.path("negativeCases")).hasSize(6);
        assertThat(fixture.path("invariants").path("doubleExecution").asInt()).isZero();
        assertThat(fixture.path("invariants").path("duplicateRevision").asInt()).isZero();
        assertThat(fixture.path("invariants").path("approvalBypass").asInt()).isZero();
        assertThat(fixture.path("invariants").path("unknownAutomaticReplay").asInt()).isZero();
        assertThat(fixture.toString()).doesNotContain("password", "credential", "payload", "real-data");
    }

    @Test
    void recoveryDecisionSetDoesNotAllowImplicitReplay() {
        assertThat(CheckpointRecoveryDecision.values()).extracting(Enum::name)
                .containsExactly("RESUME", "REPLAY_RESULT", "NEW_APPROVAL_REQUIRED", "MANUAL_RECONCILIATION");
    }

    @Test
    void leaseExpiresOnlyBeforeObservedTime() {
        TaskLease lease = new TaskLease(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "worker-syn-1",
                NOW.minusSeconds(30), NOW, NOW.plusSeconds(30), null, null, NOW, null, NOW);
        assertThat(lease.isExpiredAt(NOW.plusSeconds(30))).isFalse();
        assertThat(lease.isExpiredAt(NOW.plusSeconds(31))).isTrue();
    }

    @Test
    void scriptsAndQueryAreReadOnlyEvidenceAssets() throws IOException {
        String script = readRepoFile("scripts/qa-recovery-idempotency-lease-gate.ps1");
        String query = readRepoFile("scripts/qa-recovery-idempotency-lease-gate.sql");
        assertThat(script).contains("-Execute", "networkCalls=0", "databaseCalls=0", "ResultDirectory is required");
        assertThat(query).contains("active_leases", "invocation_count", "checkpoint_count", "packet_revision_count",
                "publication_outbox_count").doesNotContain("INSERT", "UPDATE", "DELETE");
    }

    private static JsonNode fixture() throws IOException {
        return JSON.readTree(Path.of("..", "scripts", "fixtures", "recovery-idempotency-lease-v1.json").toFile());
    }

    private static String readRepoFile(String relativePath) throws IOException {
        return Files.readString(Path.of("..", relativePath), StandardCharsets.UTF_8);
    }
}
