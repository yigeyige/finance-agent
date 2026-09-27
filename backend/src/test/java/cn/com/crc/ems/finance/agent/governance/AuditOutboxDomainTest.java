package cn.com.crc.ems.finance.agent.governance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.governance.domain.AuditDigest;
import cn.com.crc.ems.finance.agent.governance.domain.OutboxEvent;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 验证审计摘要链、规范化 JSON 和 outbox 安全边界。 */
class AuditOutboxDomainTest {

    private static final Instant NOW = Instant.parse("2026-09-27T01:00:00Z");

    @Test
    void equivalentPayloadOrderingProducesSameDigest() {
        UUID eventId = UUID.randomUUID();
        AuditEvent first = AuditEvent.create(eventId, UUID.randomUUID(), UUID.randomUUID(), "PUBLISH",
                "packet_revision", "revision-1", new VersionedJson("audit-payload-v1", "{\"b\":2,\"a\":1}"),
                NOW, null, null, NOW, null, NOW);
        AuditEvent second = AuditEvent.create(eventId, first.taskId(), first.actorUserId(), "PUBLISH",
                "packet_revision", "revision-1", new VersionedJson("audit-payload-v1", "{\"a\":1,\"b\":2}"),
                NOW, null, null, NOW, null, NOW);

        assertThat(first.payloadDigest()).isEqualTo(second.payloadDigest());
        assertThat(first.eventDigest()).isEqualTo(second.eventDigest());
        assertThat(first.hasValidIntegrity()).isTrue();
    }

    @Test
    void secondEventMustContinuePreviousDigest() {
        AuditEvent first = AuditEvent.create(UUID.randomUUID(), UUID.randomUUID(), null, "START", "task", "task-1",
                new VersionedJson("audit-payload-v1", "{}"), NOW, null, null, NOW, null, NOW);
        AuditEvent second = AuditEvent.create(UUID.randomUUID(), first.taskId(), null, "END", "task", "task-1",
                new VersionedJson("audit-payload-v1", "{}"), NOW.plusSeconds(1), first.eventDigest(), null,
                NOW.plusSeconds(1), null, NOW.plusSeconds(1));

        assertThat(second.prevEventDigest()).isEqualTo(first.eventDigest());
        assertThat(second.hasValidIntegrity()).isTrue();
        assertThatThrownBy(() -> new AuditEvent(UUID.randomUUID(), first.taskId(), null, "END", "task", "task-1",
                new VersionedJson("audit-payload-v1", "{}"), first.payloadDigest(), NOW.plusSeconds(1),
                first.eventDigest(), "f".repeat(64), AuditDigest.VERSION, null, NOW.plusSeconds(1), null,
                NOW.plusSeconds(1))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sensitivePayloadIsRejectedForAuditAndOutbox() {
        assertThatThrownBy(() -> AuditEvent.create(UUID.randomUUID(), UUID.randomUUID(), null, "READ", "task", "1",
                new VersionedJson("audit-payload-v1", "{\"reasoning_content\":\"hidden\"}"), NOW, null, null,
                NOW, null, NOW)).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new OutboxEvent(UUID.randomUUID(), "task", "1", "READ", "idem-1",
                new VersionedJson("outbox-payload-v1", "{\"access_token\":\"secret\"}"), NOW, null, 0,
                null, null, NOW)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void outboxDeliveryStateIsMonotonic() {
        OutboxEvent event = new OutboxEvent(UUID.randomUUID(), "task", "1", "READ", "idem-1",
                new VersionedJson("outbox-payload-v1", "{\"digest\":\"abc\"}"), NOW, null, 0, null, null, NOW);

        OutboxEvent attempted = event.recordDeliveryAttempt(NOW.plusSeconds(1));
        OutboxEvent delivered = attempted.markDelivered(NOW.plusSeconds(2));
        assertThat(attempted.deliveryAttempts()).isEqualTo(1);
        assertThat(delivered.deliveredAt()).isEqualTo(NOW.plusSeconds(2));
    }
}
