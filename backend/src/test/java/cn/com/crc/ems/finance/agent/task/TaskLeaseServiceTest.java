package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.application.InvestigationStepRepository;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.StepStatus;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskLeaseService;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;

class TaskLeaseServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-28T02:00:00Z");

    @Test
    void claimsOnlyApprovedStepAndWritesStateInClaimBoundary() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        InvestigationStepRepository steps = mock(InvestigationStepRepository.class);
        TaskLeaseRepository leases = mock(TaskLeaseRepository.class);
        TaskStateTransitionService transitions = mock(TaskStateTransitionService.class);
        TaskLeaseService service = new TaskLeaseService(tasks, steps, leases, transitions);
        UUID taskId = UUID.randomUUID();
        InvestigationStep step = new InvestigationStep(UUID.randomUUID(), taskId, 1, "inspect", true,
                StepStatus.APPROVED, NOW, null, null, NOW, null);
        when(steps.findNextForUpdateSkipLocked("WAITING_STEP_APPROVAL", "APPROVED"))
                .thenReturn(Optional.of(step));
        when(tasks.findByIdForUpdate(taskId)).thenReturn(Optional.of(task(taskId)));
        when(steps.updateStatus(eq(step.stepId()), eq(StepStatus.RUNNING), any(), eq(NOW))).thenReturn(1);

        assertThat(service.claimNext("worker-1", null, NOW, Duration.ofSeconds(30))).isPresent()
                .get().extracting(claim -> claim.step().status()).isEqualTo(StepStatus.APPROVED);
        verify(leases).acquire(any());
        verify(steps).updateStatus(step.stepId(), StepStatus.RUNNING, null, NOW);
        verify(transitions).transition(any());
    }

    @Test
    void claimsOnlyTheSpecifiedTaskCurrentApprovedStep() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        InvestigationStepRepository steps = mock(InvestigationStepRepository.class);
        TaskLeaseRepository leases = mock(TaskLeaseRepository.class);
        TaskStateTransitionService transitions = mock(TaskStateTransitionService.class);
        TaskLeaseService service = new TaskLeaseService(tasks, steps, leases, transitions);
        UUID taskId = UUID.randomUUID();
        UUID stepId = UUID.randomUUID();
        InvestigationStep step = new InvestigationStep(stepId, taskId, 1, "inspect", true,
                StepStatus.APPROVED, NOW, null, null, NOW, null);
        when(tasks.findByIdForUpdate(taskId)).thenReturn(Optional.of(task(taskId, stepId)));
        when(steps.findById(stepId)).thenReturn(Optional.of(step));
        when(steps.updateStatus(stepId, StepStatus.RUNNING, null, NOW)).thenReturn(1);

        assertThat(service.claim(taskId, "worker-1", null, NOW, Duration.ofSeconds(30)).step()).isEqualTo(step);
        verify(leases).acquire(any());
        verify(transitions).transition(any());
    }

    @Test
    void takeoverRejectsUnexpiredAndEqualBoundaryLeases() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        InvestigationStepRepository steps = mock(InvestigationStepRepository.class);
        TaskLeaseRepository leases = mock(TaskLeaseRepository.class);
        TaskStateTransitionService transitions = mock(TaskStateTransitionService.class);
        TaskLeaseService service = new TaskLeaseService(tasks, steps, leases, transitions);
        UUID taskId = UUID.randomUUID();
        when(tasks.findByIdForUpdate(taskId)).thenReturn(Optional.of(task(taskId)));
        when(leases.findActiveByTaskId(taskId)).thenReturn(Optional.of(newLease(taskId, NOW.plusSeconds(30))));

        assertThat(service.takeOverExpired(taskId, "worker-2", null, NOW.plusSeconds(30), Duration.ofSeconds(30)))
                .isEmpty();
        assertThat(service.takeOverExpired(taskId, "worker-2", null, NOW, Duration.ofSeconds(30))).isEmpty();
    }

    private static InvestigationTask task(UUID taskId) {
        return task(taskId, UUID.randomUUID());
    }

    private static InvestigationTask task(UUID taskId, UUID stepId) {
        return new InvestigationTask(taskId, "title", "question", UUID.randomUUID(), TaskStatus.WAITING_STEP_APPROVAL,
                UUID.randomUUID(), UUID.randomUUID(), stepId, NOW, NOW, 1, null, null, null);
    }

    private static cn.com.crc.ems.finance.agent.task.domain.TaskLease newLease(UUID taskId, Instant expiresAt) {
        return new cn.com.crc.ems.finance.agent.task.domain.TaskLease(UUID.randomUUID(), taskId, UUID.randomUUID(),
                "worker-1", NOW, NOW, expiresAt, null, null, NOW, null, NOW);
    }
}
