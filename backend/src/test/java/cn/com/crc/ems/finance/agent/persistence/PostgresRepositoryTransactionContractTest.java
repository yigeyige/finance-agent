package cn.com.crc.ems.finance.agent.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.apache.ibatis.annotations.Select;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.governance.application.AuditOutboxTransaction;
import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.governance.domain.OutboxEvent;
import cn.com.crc.ems.finance.agent.packet.application.PacketRevisionRepository;
import cn.com.crc.ems.finance.agent.packet.application.PublishTransactionCommand;
import cn.com.crc.ems.finance.agent.packet.domain.PacketRevision;
import cn.com.crc.ems.finance.agent.packet.infrastructure.TransactionalPacketPublisher;
import cn.com.crc.ems.finance.agent.shared.infrastructure.ActiveLeaseConflictException;
import cn.com.crc.ems.finance.agent.shared.infrastructure.ConcurrentRepositoryOperationException;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PostgresReadOnlyQueryService;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PostgresRepositoryExceptionMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RetryableRepositoryException;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;
import cn.com.crc.ems.finance.agent.task.infrastructure.TransactionalTaskExecutionService;
import cn.com.crc.ems.finance.agent.task.infrastructure.MybatisInvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.InvestigationTaskMapper;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.InvestigationStepMapper;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.TaskLeaseMapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

/** 验证 PostgreSQL 锁 SQL、事务入口和数据库异常映射契约。 */
class PostgresRepositoryTransactionContractTest {

    private static final Instant NOW = Instant.parse("2026-09-27T01:00:00Z");

    @Test
    void mapperSqlUsesParameterizedPostgresLocksAndStableOrdering() throws Exception {
        String taskLockSql = selectSql(InvestigationTaskMapper.class, "selectByIdForUpdate", String.class);
        String stepClaimSql = selectSql(InvestigationStepMapper.class, "selectNextForUpdateSkipLocked", String.class,
                String.class);

        assertThat(taskLockSql).contains("task_id = CAST(#{taskId} AS uuid)", "FOR UPDATE")
                .doesNotContain("+");
        assertThat(stepClaimSql).contains("#{taskStatus}", "#{stepStatus}", "ORDER BY s.created_at ASC, s.step_id ASC",
                "LIMIT 1 FOR UPDATE OF s, t SKIP LOCKED").doesNotContain("+");
        String expiredLeaseSql = selectSql(TaskLeaseMapper.class, "selectExpired", Instant.class, int.class);
        assertThat(expiredLeaseSql).contains("released_at IS NULL", "expires_at < #{observedAt}",
                "ORDER BY expires_at ASC, lease_id ASC", "LIMIT #{limit}").doesNotContain("+");
    }

    @Test
    void leaseAcquisitionLocksTaskAndMapsUniqueConflict() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        TaskLeaseRepository leases = mock(TaskLeaseRepository.class);
        UUID taskId = UUID.randomUUID();
        TaskLease lease = lease(taskId);
        when(tasks.findByIdForUpdate(taskId)).thenReturn(Optional.of(task(taskId, TaskStatus.READY, 1)));
        doThrow(new DuplicateKeyException("duplicate")).when(leases).acquire(lease);

        assertThatThrownBy(() -> new TransactionalTaskExecutionService(tasks, leases).acquireLease(lease))
                .isInstanceOf(ActiveLeaseConflictException.class);
        verify(tasks).findByIdForUpdate(taskId);
    }

    @Test
    void optimisticConflictHasExplicitOutcome() {
        InvestigationTaskMapper mapper = mock(InvestigationTaskMapper.class);
        InvestigationTask task = task(UUID.randomUUID(), TaskStatus.READY, 2);
        when(mapper.update(any(), any(UpdateWrapper.class))).thenReturn(0);
        InvestigationTaskRepository tasks = new MybatisInvestigationTaskRepository(mapper);

        assertThatThrownBy(() -> tasks.updateWithVersionOrThrow(task, 1))
                .isInstanceOf(ConcurrentRepositoryOperationException.class);
    }

    @Test
    void postgresDeadlockStateMapsToRetryableFailureWithoutSqlMessage() {
        RuntimeException failure = new RuntimeException(new SQLException("database detail", "40P01"));

        assertThat(PostgresRepositoryExceptionMapper.map("publish", failure))
                .isInstanceOf(RetryableRepositoryException.class)
                .hasMessage("publish failed and may be retried");
    }

    @Test
    void publicationServiceUsesOneTransactionalWriteBoundary() throws Exception {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        PacketRevisionRepository revisions = mock(PacketRevisionRepository.class);
        AuditOutboxTransaction auditOutbox = mock(AuditOutboxTransaction.class);
        UUID taskId = UUID.randomUUID();
        InvestigationTask current = task(taskId, TaskStatus.READY_TO_PUBLISH, 3);
        InvestigationTask published = task(taskId, TaskStatus.PUBLISHED, 3);
        PacketRevision revision = revision(taskId);
        AuditEvent audit = AuditEvent.create(UUID.randomUUID(), taskId, UUID.randomUUID(), "PACKET_PUBLISHED", "packet",
                revision.packetRevisionId().toString(), new VersionedJson("audit-payload-v1", "{}"), NOW, null, null, NOW,
                null, NOW);
        OutboxEvent outbox = new OutboxEvent(UUID.randomUUID(), "packet", revision.packetRevisionId().toString(),
                "PACKET_PUBLISHED", "packet-published-1", new VersionedJson("outbox-payload-v1", "{}"), NOW, null, 0,
                null, null, NOW);
        when(tasks.findByIdForUpdate(taskId)).thenReturn(Optional.of(current));

        TransactionalPacketPublisher publisher = new TransactionalPacketPublisher(tasks, revisions, auditOutbox);
        assertThat(publisher.publish(new PublishTransactionCommand(taskId, published, revision, audit, outbox)))
                .isEqualTo(revision);

        verify(revisions).insert(revision);
        verify(auditOutbox).append(audit, outbox);
        verify(tasks).updateWithVersionOrThrow(published, 3);
        assertThat(TransactionalPacketPublisher.class.getMethod("publish", PublishTransactionCommand.class)
                .getAnnotation(Transactional.class)).isNotNull();
    }

    @Test
    void alreadyCommittedPublicationReplaysSameRevision() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        PacketRevisionRepository revisions = mock(PacketRevisionRepository.class);
        AuditOutboxTransaction auditOutbox = mock(AuditOutboxTransaction.class);
        UUID taskId = UUID.randomUUID();
        PacketRevision revision = revision(taskId);
        when(revisions.findById(revision.packetRevisionId())).thenReturn(Optional.of(revision));
        AuditEvent audit = AuditEvent.create(UUID.randomUUID(), taskId, UUID.randomUUID(), "PACKET_PUBLISHED", "packet",
                revision.packetRevisionId().toString(), new VersionedJson("audit-payload-v1", "{}"), NOW, null, null, NOW,
                null, NOW);
        OutboxEvent outbox = new OutboxEvent(UUID.randomUUID(), "packet", revision.packetRevisionId().toString(),
                "PACKET_PUBLISHED", "packet-published-replay", new VersionedJson("outbox-payload-v1", "{}"), NOW,
                null, 0, null, null, NOW);

        PublishTransactionCommand command = new PublishTransactionCommand(taskId,
                task(taskId, TaskStatus.PUBLISHED, 4), revision, audit, outbox);

        assertThat(new TransactionalPacketPublisher(tasks, revisions, auditOutbox).publish(command))
                .isSameAs(revision);
        verify(revisions).findById(revision.packetRevisionId());
    }

    @Test
    void readOnlyFacadeDeclaresReadOnlyTransactions() throws Exception {
        Method method = PostgresReadOnlyQueryService.class.getMethod("findTask", UUID.class);
        assertThat(method.getAnnotation(Transactional.class).readOnly()).isTrue();
    }

    private static String selectSql(Class<?> mapper, String methodName, Class<?>... parameterTypes) throws Exception {
        return String.join(" ", mapper.getMethod(methodName, parameterTypes).getAnnotation(Select.class).value());
    }

    private static InvestigationTask task(UUID taskId, TaskStatus status, long rowVersion) {
        return new InvestigationTask(taskId, "title", "question", UUID.randomUUID(), status, UUID.randomUUID(), null,
                null, null, NOW, NOW, rowVersion, null, null, UUID.randomUUID());
    }

    private static TaskLease lease(UUID taskId) {
        return new TaskLease(UUID.randomUUID(), taskId, UUID.randomUUID(), "worker-1", NOW, NOW,
                NOW.plusSeconds(60), null, null, NOW, null, NOW);
    }

    private static PacketRevision revision(UUID taskId) {
        return new PacketRevision(UUID.randomUUID(), taskId, 1, UUID.randomUUID(),
                new VersionedJson("packet-v1", "{}"), "<html>packet</html>", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                NOW, new VersionedJson("runtime-v1", "{}"), new VersionedJson("coverage-v1", "{}"), UUID.randomUUID(), NOW,
                null, null, NOW, null, NOW);
    }
}
