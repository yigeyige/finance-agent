package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationRepository;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationService;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationStartCommand;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationStartResult;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStateConflictException;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocationKey;

class ToolInvocationServiceTest {

    private static final UUID ACTOR = UUID.randomUUID();
    private static final Instant NOW = Instant.parse("2026-09-28T00:00:00Z");

    @Test
    void firstRequestRecordsIntentAndAllowsExecution() {
        ToolInvocationRepository repository = org.mockito.Mockito.mock(ToolInvocationRepository.class);
        ToolInvocationService service = new ToolInvocationService(repository);
        ToolInvocationStartCommand command = command();

        ToolInvocationStartResult result = service.startIntent(command);

        assertThat(result.action()).isEqualTo(ToolInvocationStartResult.Action.EXECUTE);
        assertThat(result.invocation().status()).isEqualTo(InvocationStatus.INTENT_RECORDED);
        assertThat(result.invocation().idempotencyKey()).isEqualTo(command.key().value());
        verify(repository).insert(any(ToolInvocation.class));
    }

    @Test
    void completedRequestReplaysPersistedResult() {
        ToolInvocationRepository repository = org.mockito.Mockito.mock(ToolInvocationRepository.class);
        ToolInvocation existing = invocation(InvocationStatus.SUCCEEDED);
        when(repository.findByIdempotencyKey(existing.idempotencyKey())).thenReturn(Optional.of(existing));

        ToolInvocationStartResult result = new ToolInvocationService(repository)
                .startIntent(command(existing.idempotencyKey()));

        assertThat(result.action()).isEqualTo(ToolInvocationStartResult.Action.REPLAY);
        assertThat(result.invocation()).isSameAs(existing);
    }

    @Test
    void unknownRequestRequiresManualReconciliation() {
        ToolInvocationRepository repository = org.mockito.Mockito.mock(ToolInvocationRepository.class);
        ToolInvocation existing = invocation(InvocationStatus.UNKNOWN);
        when(repository.findByIdempotencyKey(existing.idempotencyKey())).thenReturn(Optional.of(existing));

        ToolInvocationStartResult result = new ToolInvocationService(repository)
                .startIntent(command(existing.idempotencyKey()));

        assertThat(result.action()).isEqualTo(ToolInvocationStartResult.Action.MANUAL_RECONCILIATION);
    }

    @Test
    void duplicateInsertRaceReadsTheWinner() {
        ToolInvocationRepository repository = org.mockito.Mockito.mock(ToolInvocationRepository.class);
        ToolInvocation winner = invocation(InvocationStatus.INTENT_RECORDED);
        when(repository.findByIdempotencyKey(winner.idempotencyKey())).thenReturn(Optional.empty(), Optional.of(winner));
        doThrow(new DuplicateKeyException("duplicate")).when(repository).insert(any(ToolInvocation.class));

        ToolInvocationStartResult result = new ToolInvocationService(repository)
                .startIntent(command(winner.idempotencyKey()));

        assertThat(result.action()).isEqualTo(ToolInvocationStartResult.Action.IN_PROGRESS);
    }

    @Test
    void terminalResultCannotBeOverwritten() {
        ToolInvocationRepository repository = org.mockito.Mockito.mock(ToolInvocationRepository.class);
        ToolInvocation existing = invocation(InvocationStatus.SUCCEEDED);
        when(repository.findByIdempotencyKey(existing.idempotencyKey())).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> new ToolInvocationService(repository).complete(existing.idempotencyKey(),
                InvocationStatus.FAILED, null, "TIMEOUT", ACTOR, NOW))
                .isInstanceOf(InvocationStateConflictException.class);
    }

    @Test
    void successfulCompletionPersistsResponseDigest() {
        ToolInvocationRepository repository = org.mockito.Mockito.mock(ToolInvocationRepository.class);
        ToolInvocation current = invocation(InvocationStatus.RUNNING);
        ToolInvocation completed = new ToolInvocation(current.invocationId(), current.taskId(), current.stepId(),
                current.approvalId(), current.toolCode(), current.idempotencyKey(), current.requestDigest(),
                Sha256Digest.hex("response"), InvocationStatus.SUCCEEDED, current.startedAt(), NOW, null,
                ACTOR, current.createdAt(), ACTOR, NOW);
        when(repository.findByIdempotencyKey(current.idempotencyKey())).thenReturn(Optional.of(current),
                Optional.of(completed));
        when(repository.updateStatusIfCurrent(current.idempotencyKey(), InvocationStatus.RUNNING,
                InvocationStatus.SUCCEEDED, Sha256Digest.hex("response"), null, NOW, NOW, ACTOR)).thenReturn(true);

        assertThat(new ToolInvocationService(repository).complete(current.idempotencyKey(), InvocationStatus.SUCCEEDED,
                Sha256Digest.hex("response"), null, ACTOR, NOW)).isSameAs(completed);
    }

    @Test
    void unknownCompletionRequiresAnErrorCode() {
        ToolInvocationRepository repository = org.mockito.Mockito.mock(ToolInvocationRepository.class);
        ToolInvocation existing = invocation(InvocationStatus.RUNNING);
        when(repository.findByIdempotencyKey(existing.idempotencyKey())).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> new ToolInvocationService(repository).complete(existing.idempotencyKey(),
                InvocationStatus.UNKNOWN, null, null, ACTOR, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static ToolInvocationStartCommand command() {
        ToolInvocationKey key = new ToolInvocationKey(UUID.randomUUID(), UUID.randomUUID(), 1, 1);
        return new ToolInvocationStartCommand(key, UUID.randomUUID(), "read-config", Sha256Digest.hex("request"),
                ACTOR, NOW);
    }

    private static ToolInvocationStartCommand command(String idempotencyKey) {
        String[] parts = idempotencyKey.split(":");
        ToolInvocationKey key = new ToolInvocationKey(UUID.fromString(parts[0]), UUID.fromString(parts[1]),
                Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        return new ToolInvocationStartCommand(key, UUID.randomUUID(), "read-config", Sha256Digest.hex("request"),
                ACTOR, NOW);
    }

    private static ToolInvocation invocation(InvocationStatus status) {
        UUID taskId = UUID.randomUUID();
        UUID stepId = UUID.randomUUID();
        return new ToolInvocation(UUID.randomUUID(), taskId, stepId, UUID.randomUUID(), "read-config",
                new ToolInvocationKey(taskId, stepId, 1, 1).value(), Sha256Digest.hex("request"),
                status == InvocationStatus.SUCCEEDED ? Sha256Digest.hex("response") : null, status, NOW,
                status == InvocationStatus.INTENT_RECORDED || status == InvocationStatus.RUNNING ? null : NOW,
                status == InvocationStatus.UNKNOWN ? "TIMEOUT" : null, ACTOR, NOW, ACTOR, NOW);
    }
}
