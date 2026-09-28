package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.application.TaskCancelCommand;
import cn.com.crc.ems.finance.agent.task.application.TaskControlErrorCode;
import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskPauseCommand;
import cn.com.crc.ems.finance.agent.task.application.TaskStateEventRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionCommand;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionResult;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateEvent;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateMachine;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskControlService;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;

class TaskControlServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-28T00:00:00Z");

    @Test
    void pauseMapsBudgetErrorToPausedBudget() {
        TaskStateTransitionService transitions = mock(TaskStateTransitionService.class);
        TaskLeaseRepository leases = mock(TaskLeaseRepository.class);
        TaskControlService service = new TaskControlService(transitions, leases, mockTaskRepository());
        when(transitions.transition(any(TaskStateTransitionCommand.class))).thenAnswer(invocation -> null);

        service.pause(new TaskPauseCommand(UUID.randomUUID(), TaskControlErrorCode.BUDGET_EXHAUSTED,
                "budget exhausted", UUID.randomUUID(), NOW));

        verify(transitions).transition(org.mockito.ArgumentMatchers.argThat(command ->
                command.targetStatus() == TaskStatus.PAUSED_BUDGET));
    }

    @Test
    void cancelReleasesActiveLeaseWithoutDeletingTask() {
        TaskStateTransitionService transitions = mock(TaskStateTransitionService.class);
        TaskLeaseRepository leases = mock(TaskLeaseRepository.class);
        InvestigationTask task = task();
        when(transitions.transition(any(TaskStateTransitionCommand.class))).thenReturn(
                new TaskStateTransitionResult(task, new TaskStateEvent(UUID.randomUUID(), task.taskId(),
                        TaskStatus.RUNNING_STEP, TaskStatus.CANCELLED, "cancel", task.initiatorUserId(), NOW, 2)));
        TaskLease lease = new TaskLease(UUID.randomUUID(), task.taskId(), task.currentStepId(), "worker-1", NOW,
                NOW, NOW.plusSeconds(60), null, task.initiatorUserId(), NOW, task.initiatorUserId(), NOW);
        when(leases.findActiveByTaskId(task.taskId())).thenReturn(Optional.of(lease));
        when(leases.release(lease.leaseId(), lease.ownerInstance(), NOW, task.initiatorUserId())).thenReturn(true);
        TaskControlService service = new TaskControlService(transitions, leases, mockTaskRepository());

        service.cancel(new TaskCancelCommand(task.taskId(), "operator cancel", task.initiatorUserId(), NOW));

        verify(leases).release(lease.leaseId(), lease.ownerInstance(), NOW, task.initiatorUserId());
    }

    private static cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository mockTaskRepository() {
        return mock(cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository.class);
    }

    private static InvestigationTask task() {
        UUID user = UUID.randomUUID();
        return new InvestigationTask(UUID.randomUUID(), "title", "question", user, TaskStatus.RUNNING_STEP,
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), NOW, NOW, 1, null, user, user);
    }
}
