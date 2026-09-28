package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.CheckpointRecoveryDecision;
import cn.com.crc.ems.finance.agent.harness.application.CheckpointRecoveryRequest;
import cn.com.crc.ems.finance.agent.harness.application.CheckpointRecoveryService;
import cn.com.crc.ems.finance.agent.harness.application.CheckpointRepository;
import cn.com.crc.ems.finance.agent.harness.application.CheckpointBusinessContextRepository;
import cn.com.crc.ems.finance.agent.harness.application.SessionStateRepository;
import cn.com.crc.ems.finance.agent.harness.domain.Checkpoint;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;
import cn.com.crc.ems.finance.agent.harness.domain.SessionState;
import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocationKey;

class CheckpointRecoveryServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-28T00:00:00Z");
    private static final String DIGEST = "a".repeat(64);

    @Test
    void compatibleCheckpointResumesWithoutSideEffects() {
        Fixture fixture = fixture();
        CheckpointRecoveryService service = fixture.service(Optional.empty());

        assertThat(service.decide(fixture.request(null)).decision())
                .isEqualTo(CheckpointRecoveryDecision.RESUME);
    }

    @Test
    void runtimeDriftRequiresNewApproval() {
        Fixture fixture = fixture();
        when(fixture.contexts.find(fixture.taskId, fixture.stepId, fixture.scopeId, fixture.approvalId)).thenReturn(Optional.of(
                new CheckpointBusinessContextRepository.CheckpointBusinessContext(fixture.taskId, fixture.stepId,
                        fixture.scopeId, UUID.randomUUID(), true, true, true, true, NOW.plusSeconds(600), 3, 0)));

        assertThat(fixture.service(Optional.empty()).decide(fixture.request(null)).decision())
                .isEqualTo(CheckpointRecoveryDecision.NEW_APPROVAL_REQUIRED);
    }

    @Test
    void persistedResultIsReplayed() {
        Fixture fixture = fixture();
        String key = new ToolInvocationKey(fixture.taskId, fixture.stepId, 1, 1).value();
        ToolInvocation invocation = new ToolInvocation(UUID.randomUUID(), fixture.taskId, fixture.stepId,
                fixture.approvalId, "read-config", key, DIGEST, DIGEST, InvocationStatus.SUCCEEDED, NOW, NOW,
                null, fixture.userId, NOW, fixture.userId, NOW);

        assertThat(fixture.service(Optional.of(invocation)).decide(fixture.request(key)).decision())
                .isEqualTo(CheckpointRecoveryDecision.REPLAY_RESULT);
    }

    @Test
    void unknownInvocationRequiresManualReconciliation() {
        Fixture fixture = fixture();
        String key = new ToolInvocationKey(fixture.taskId, fixture.stepId, 1, 1).value();
        ToolInvocation invocation = new ToolInvocation(UUID.randomUUID(), fixture.taskId, fixture.stepId,
                fixture.approvalId, "read-config", key, DIGEST, null, InvocationStatus.UNKNOWN, NOW, NOW,
                "NETWORK_TIMEOUT", fixture.userId, NOW, fixture.userId, NOW);

        assertThat(fixture.service(Optional.of(invocation)).decide(fixture.request(key)).decision())
                .isEqualTo(CheckpointRecoveryDecision.MANUAL_RECONCILIATION);
    }

    private static Fixture fixture() {
        return new Fixture();
    }

    private static final class Fixture {
        final UUID taskId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();
        final UUID sessionId = UUID.randomUUID();
        final UUID stepId = UUID.randomUUID();
        final UUID scopeId = UUID.randomUUID();
        final UUID approvalId = UUID.randomUUID();
        final UUID runtimeId = UUID.randomUUID();
        final Checkpoint checkpoint = new Checkpoint(UUID.randomUUID(), sessionId, taskId, userId, stepId, scopeId,
                approvalId, runtimeId, "RESULT_TRIMMED", new StateReference("harness-v1", "{\"safe\":true}", DIGEST),
                userId, NOW, userId, NOW);
        final CheckpointRepository checkpoints = mock(CheckpointRepository.class);
        final SessionStateRepository sessions = mock(SessionStateRepository.class);
        final CheckpointBusinessContextRepository contexts = mock(CheckpointBusinessContextRepository.class);
        final TaskLeaseRepository leases = mock(TaskLeaseRepository.class);
        final ToolInvocationRepository invocations = mock(ToolInvocationRepository.class);

        Fixture() {
            when(checkpoints.findById(checkpoint.checkpointId(), taskId, userId)).thenReturn(Optional.of(checkpoint));
            when(contexts.find(taskId, stepId, scopeId, approvalId)).thenReturn(Optional.of(
                    new CheckpointBusinessContextRepository.CheckpointBusinessContext(taskId, stepId, scopeId, runtimeId,
                            true, true, true, true, NOW.plusSeconds(600), 3, 0)));
            when(sessions.findById(sessionId, taskId, userId)).thenReturn(Optional.of(new SessionState(sessionId, taskId,
                    userId, "harness-2.0", new StateReference("harness-v1", "{\"safe\":true}", DIGEST), userId,
                    NOW, userId, NOW)));
            when(leases.findActiveByTaskId(taskId)).thenReturn(Optional.empty());
        }

        CheckpointRecoveryService service(Optional<ToolInvocation> invocation) {
            when(invocations.findByIdempotencyKey(org.mockito.ArgumentMatchers.anyString())).thenReturn(invocation);
            return new CheckpointRecoveryService(checkpoints, sessions, contexts, leases, invocations);
        }

        CheckpointRecoveryRequest request(String key) {
            return new CheckpointRecoveryRequest(checkpoint.checkpointId(), taskId, userId, sessionId, null, key, NOW);
        }
    }

}
