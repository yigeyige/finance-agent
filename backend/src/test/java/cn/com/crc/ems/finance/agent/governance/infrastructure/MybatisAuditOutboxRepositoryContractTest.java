package cn.com.crc.ems.finance.agent.governance.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.governance.domain.OutboxEvent;
import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.AuditEventEntity;
import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.OutboxEventEntity;
import cn.com.crc.ems.finance.agent.governance.infrastructure.mapper.AuditEventMapper;
import cn.com.crc.ems.finance.agent.governance.infrastructure.mapper.OutboxEventMapper;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 验证审计和 outbox 仓储的查询隔离、追加与幂等入口。 */
class MybatisAuditOutboxRepositoryContractTest {

    private static final Instant NOW = Instant.parse("2026-09-27T01:00:00Z");

    @Test
    void auditRepositoryRequiresCurrentChainHeadBeforeAppend() {
        AuditEventMapper mapper = org.mockito.Mockito.mock(AuditEventMapper.class);
        AuditEventRepositoryFixture fixture = new AuditEventRepositoryFixture(mapper);
        AuditEvent event = AuditEvent.create(UUID.randomUUID(), UUID.randomUUID(), null, "START", "task", "1",
                new VersionedJson("audit-payload-v1", "{}"), NOW, null, null, NOW, null, NOW);
        when(mapper.selectList(any(QueryWrapper.class))).thenReturn(List.of());

        assertThat(fixture.repository.append(event)).isEqualTo(event);
        verify(mapper).insert(any(AuditEventEntity.class));
        verify(mapper).selectList(any(QueryWrapper.class));
    }

    @Test
    void outboxRepositoryReturnsExistingIdempotencyRecord() {
        OutboxEventMapper mapper = org.mockito.Mockito.mock(OutboxEventMapper.class);
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.outboxId = UUID.randomUUID().toString();
        entity.aggregateType = "task";
        entity.aggregateId = "1";
        entity.eventType = "START";
        entity.idempotencyKey = "same-key";
        entity.payload = "{\"digest\":\"abc\"}";
        entity.createdAt = NOW;
        entity.deliveryAttempts = 0;
        entity.updatedAt = NOW;
        when(mapper.selectOne(any(QueryWrapper.class))).thenReturn(entity);

        OutboxEvent event = new OutboxEvent(UUID.randomUUID(), "task", "1", "START", "same-key",
                new VersionedJson("outbox-payload-v1", "{\"digest\":\"abc\"}"), NOW, null, 0, null, null, NOW);
        OutboxEvent result = new MybatisOutboxEventRepository(mapper).insertIfAbsent(event);

        assertThat(result.outboxId()).isEqualTo(UUID.fromString(entity.outboxId));
    }

    private static final class AuditEventRepositoryFixture {
        private final MybatisAuditEventRepository repository;

        private AuditEventRepositoryFixture(AuditEventMapper mapper) {
            this.repository = new MybatisAuditEventRepository(mapper);
        }
    }
}
