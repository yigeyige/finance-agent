package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.CheckpointRecordCommand;
import cn.com.crc.ems.finance.agent.harness.application.CheckpointRepository;
import cn.com.crc.ems.finance.agent.harness.application.CheckpointBusinessContextRepository;
import cn.com.crc.ems.finance.agent.harness.application.CheckpointService;
import cn.com.crc.ems.finance.agent.harness.application.SessionStateRepository;
import cn.com.crc.ems.finance.agent.harness.domain.Checkpoint;
import cn.com.crc.ems.finance.agent.harness.domain.CheckpointType;
import cn.com.crc.ems.finance.agent.harness.domain.SessionState;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;

class CheckpointServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-28T00:00:00Z");
    private static final String DIGEST = "a".repeat(64);

    @Test
    void recordsOnlyCurrentRuntimeAndApprovalBinding() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        UUID stepId = UUID.randomUUID();
        UUID scopeId = UUID.randomUUID();
        UUID approvalId = UUID.randomUUID();
        UUID runtimeId = UUID.randomUUID();
        CheckpointRepository checkpoints = mock(CheckpointRepository.class);
        SessionStateRepository sessions = mock(SessionStateRepository.class);
        CheckpointBusinessContextRepository contexts = mock(CheckpointBusinessContextRepository.class);
        when(sessions.findById(sessionId, taskId, userId)).thenReturn(Optional.of(new SessionState(sessionId, taskId,
                userId, "harness-2.0", new StateReference("harness-v1", "{}", DIGEST), userId, NOW, userId, NOW)));
        when(contexts.find(taskId, stepId, scopeId, approvalId)).thenReturn(Optional.of(
                new CheckpointBusinessContextRepository.CheckpointBusinessContext(taskId, stepId, scopeId, runtimeId,
                        true, true, true, true, NOW.plusSeconds(600), 3, 0)));

        Checkpoint checkpoint = new CheckpointService(checkpoints, sessions, contexts)
                .record(new CheckpointRecordCommand(sessionId, taskId, userId, stepId, scopeId, approvalId, runtimeId,
                        CheckpointType.RESULT_TRIMMED, new StateReference("harness-v1", "{\"safe\":true}", DIGEST),
                        userId, NOW));

        assertThat(checkpoint.runtimeBundleId()).isEqualTo(runtimeId);
        assertThat(checkpoint.checkpointType()).isEqualTo("RESULT_TRIMMED");
        verify(checkpoints).insert(any(Checkpoint.class));
    }

}
