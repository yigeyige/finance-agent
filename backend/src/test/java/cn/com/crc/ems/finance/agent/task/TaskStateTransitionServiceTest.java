package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.shared.infrastructure.ConcurrentRepositoryOperationException;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskStateEventRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionCommand;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionResult;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateEvent;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateMachine;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;

/** 验证任务状态应用事务的锁定、版本更新和事件追加顺序。 */
class TaskStateTransitionServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-27T01:00:00Z");

    @Test
    void successfulTransitionUpdatesVersionAndAppendsEvent() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        TaskStateEventRepository events = mock(TaskStateEventRepository.class);
        InvestigationTask current = task(TaskStatus.DRAFT, 4);
        when(tasks.findByIdForUpdate(current.taskId())).thenReturn(Optional.of(current));
        doCallRealMethod().when(tasks).updateWithVersionOrThrow(any(), eq(4L));
        when(tasks.updateWithVersion(any(), eq(4L))).thenReturn(true);
        when(events.append(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TaskStateTransitionResult result = new TaskStateTransitionService(tasks, events, new TaskStateMachine())
                .transition(new TaskStateTransitionCommand(current.taskId(), TaskStatus.WAITING_SCOPE_CONFIRMATION,
                        "submit-scope", UUID.randomUUID(), NOW, false));

        assertThat(result.task().status()).isEqualTo(TaskStatus.WAITING_SCOPE_CONFIRMATION);
        assertThat(result.task().rowVersion()).isEqualTo(5);
        assertThat(result.event().fromStatus()).isEqualTo(TaskStatus.DRAFT);
        assertThat(result.event().rowVersion()).isEqualTo(5);
        verify(tasks).findByIdForUpdate(current.taskId());
        verify(tasks).updateWithVersionOrThrow(result.task(), 4);
        verify(events).append(result.event());
    }

    @Test
    void invalidTransitionHasNoRepositorySideEffect() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        TaskStateEventRepository events = mock(TaskStateEventRepository.class);
        InvestigationTask current = task(TaskStatus.READY, 1);
        when(tasks.findByIdForUpdate(current.taskId())).thenReturn(Optional.of(current));

        assertThatThrownBy(() -> new TaskStateTransitionService(tasks, events, new TaskStateMachine()).transition(
                new TaskStateTransitionCommand(current.taskId(), TaskStatus.PUBLISHED, "skip", null, NOW, false)))
                .isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(events);
        verify(tasks, org.mockito.Mockito.never()).updateWithVersionOrThrow(any(), any(Long.class));
    }

    @Test
    void versionConflictPreventsEventAppend() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        TaskStateEventRepository events = mock(TaskStateEventRepository.class);
        InvestigationTask current = task(TaskStatus.DRAFT, 2);
        when(tasks.findByIdForUpdate(current.taskId())).thenReturn(Optional.of(current));
        doThrow(new ConcurrentRepositoryOperationException("version changed"))
                .when(tasks).updateWithVersionOrThrow(any(), eq(2L));

        assertThatThrownBy(() -> new TaskStateTransitionService(tasks, events, new TaskStateMachine()).transition(
                new TaskStateTransitionCommand(current.taskId(), TaskStatus.WAITING_SCOPE_CONFIRMATION, "submit", null,
                        NOW, false))).isInstanceOf(ConcurrentRepositoryOperationException.class);
        verifyNoInteractions(events);
    }

    @Test
    void eventFailureIsPropagatedAfterStateUpdateForTransactionRollback() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        TaskStateEventRepository events = mock(TaskStateEventRepository.class);
        InvestigationTask current = task(TaskStatus.DRAFT, 3);
        when(tasks.findByIdForUpdate(current.taskId())).thenReturn(Optional.of(current));
        doCallRealMethod().when(tasks).updateWithVersionOrThrow(any(), eq(3L));
        when(tasks.updateWithVersion(any(), eq(3L))).thenReturn(true);
        doThrow(new IllegalStateException("audit unavailable")).when(events).append(any(TaskStateEvent.class));

        assertThatThrownBy(() -> new TaskStateTransitionService(tasks, events, new TaskStateMachine()).transition(
                new TaskStateTransitionCommand(current.taskId(), TaskStatus.WAITING_SCOPE_CONFIRMATION, "submit", null,
                        NOW, false))).isInstanceOf(IllegalStateException.class);
        verify(tasks).updateWithVersionOrThrow(any(), eq(3L));
        verify(events).append(any(TaskStateEvent.class));
    }

    @Test
    void serviceDeclaresTransactionalBoundary() throws Exception {
        assertThat(TaskStateTransitionService.class.getMethod("transition", TaskStateTransitionCommand.class)
                .getAnnotation(Transactional.class)).isNotNull();
    }

    private static InvestigationTask task(TaskStatus status, long rowVersion) {
        return new InvestigationTask(UUID.randomUUID(), "title", "question", UUID.randomUUID(), status,
                UUID.randomUUID(), null, null, null, NOW, NOW, rowVersion, null, null, UUID.randomUUID());
    }
}
