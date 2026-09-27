package cn.com.crc.ems.finance.agent.governance.infrastructure;

import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.governance.application.AuditEventRepository;
import cn.com.crc.ems.finance.agent.governance.application.OutboxEventRepository;
import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.governance.domain.OutboxEvent;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 验证事务写入器同时调用审计与 outbox 仓储。 */
class TransactionalAuditOutboxWriterTest {

    @Test
    void appendsBothRecordsThroughOneTransactionalUseCase() {
        AuditEventRepository audit = org.mockito.Mockito.mock(AuditEventRepository.class);
        OutboxEventRepository outbox = org.mockito.Mockito.mock(OutboxEventRepository.class);
        TransactionalAuditOutboxWriter writer = new TransactionalAuditOutboxWriter(audit, outbox);
        Instant now = Instant.parse("2026-09-27T01:00:00Z");
        AuditEvent auditEvent = AuditEvent.create(UUID.randomUUID(), UUID.randomUUID(), null, "START", "task", "1",
                new VersionedJson("audit-payload-v1", "{}"), now, null, null, now, null, now);
        OutboxEvent outboxEvent = new OutboxEvent(UUID.randomUUID(), "task", "1", "START", "idem-1",
                new VersionedJson("outbox-payload-v1", "{\"digest\":\"abc\"}"), now, null, 0, null, null, now);

        writer.append(auditEvent, outboxEvent);

        verify(audit).append(auditEvent);
        verify(outbox).insertIfAbsent(outboxEvent);
    }
}
