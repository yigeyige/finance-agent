package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.application.AddTaskPremiseCommand;
import cn.com.crc.ems.finance.agent.task.application.ConfirmScopeCommand;
import cn.com.crc.ems.finance.agent.task.application.CreateTaskDraftCommand;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.ScopeConfirmationResult;
import cn.com.crc.ems.finance.agent.task.application.ScopeRevisionRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskIntakeApplicationService;
import cn.com.crc.ems.finance.agent.task.application.TaskPremiseRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskStateEventRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionResult;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionCommand;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;
import cn.com.crc.ems.finance.agent.task.domain.TaskPremise;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateEvent;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateMachine;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;
import cn.com.crc.ems.finance.agent.task.application.StepApprovalRepository;
import cn.com.crc.ems.finance.agent.packet.application.PacketDraftRepository;

/** 验证任务发起、前提来源和范围确认的应用边界。 */
class TaskIntakeApplicationServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-27T02:00:00Z");

    @Test
    void createsDraftWithoutStartingExecution() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        TaskPremiseRepository premises = mock(TaskPremiseRepository.class);
        ScopeRevisionRepository scopes = mock(ScopeRevisionRepository.class);
        TaskAuditEventAppender audit = mock(TaskAuditEventAppender.class);
        TaskIntakeApplicationService service = service(tasks, premises, scopes, audit, mock(TaskStateTransitionService.class));

        InvestigationTask task = service.createTask(new CreateTaskDraftCommand("title", "question",
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), NOW));

        assertThat(task.status()).isEqualTo(TaskStatus.DRAFT);
        assertThat(task.currentScopeRevisionId()).isNull();
        verify(tasks).insert(task);
        verify(audit).append(any(), any(), any(), org.mockito.ArgumentMatchers.eq("TASK_CREATED"), any(), any(), any(), any());
    }

    @Test
    void storesPremiseAsUnverifiedOperatorInput() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        TaskPremiseRepository premises = mock(TaskPremiseRepository.class);
        ScopeRevisionRepository scopes = mock(ScopeRevisionRepository.class);
        TaskAuditEventAppender audit = mock(TaskAuditEventAppender.class);
        InvestigationTask task = task(TaskStatus.DRAFT, null, 0);
        when(tasks.findByIdForUpdate(task.taskId())).thenReturn(Optional.of(task));
        TaskIntakeApplicationService service = service(tasks, premises, scopes, audit, mock(TaskStateTransitionService.class));

        TaskPremise premise = service.addPremise(new AddTaskPremiseCommand(task.taskId(), "OPERATOR_PROVIDED",
                "EMP-SYN-001", "已离职", UUID.randomUUID(), "运维人员输入", NOW));

        assertThat(premise.independentlyVerified()).isFalse();
        verify(premises).insert(premise);
        verify(audit).append(any(), any(), any(), org.mockito.ArgumentMatchers.eq("TASK_PREMISE_ADDED"), any(), any(), any(), any());
    }

    @Test
    void confirmsImmutableScopeAndTransitionsDraftThroughWaitingToReady() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        TaskPremiseRepository premises = mock(TaskPremiseRepository.class);
        ScopeRevisionRepository scopes = mock(ScopeRevisionRepository.class);
        TaskAuditEventAppender audit = mock(TaskAuditEventAppender.class);
        TaskStateTransitionService state = mock(TaskStateTransitionService.class);
        InvestigationTask draft = task(TaskStatus.DRAFT, null, 0);
        InvestigationTask waiting = task(TaskStatus.WAITING_SCOPE_CONFIRMATION, null, 1);
        InvestigationTask ready = task(TaskStatus.READY, UUID.randomUUID(), 2);
        when(tasks.findByIdForUpdate(draft.taskId())).thenReturn(Optional.of(draft));
        when(scopes.findLatestByTaskId(draft.taskId())).thenReturn(Optional.empty());
        when(state.transition(any(TaskStateTransitionCommand.class))).thenReturn(
                result(waiting), result(ready));
        TaskIntakeApplicationService service = service(tasks, premises, scopes, audit, state);

        ScopeConfirmationResult result = service.confirmScope(new ConfirmScopeCommand(draft.taskId(), "FIN-SYN",
                "{\"module\":\"approval\"}", "{\"from\":\"2026-01-01\"}", "[\"workflow\"]", "[]",
                "[\"workflow.id\"]", "[\"workflow.id\"]", "[\"workflow.id\"]", "scope-v1",
                UUID.randomUUID(), NOW));

        assertThat(result.scopeRevision().revisionNo()).isOne();
        assertThat(result.scopeRevision().scopeDigest()).hasSize(64);
        verify(scopes).insert(result.scopeRevision());
        assertThat(result.task()).isEqualTo(ready);
    }

    @Test
    void invalidScopeDoesNotWriteRevision() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        TaskPremiseRepository premises = mock(TaskPremiseRepository.class);
        ScopeRevisionRepository scopes = mock(ScopeRevisionRepository.class);
        TaskAuditEventAppender audit = mock(TaskAuditEventAppender.class);
        InvestigationTask task = task(TaskStatus.DRAFT, null, 0);
        when(tasks.findByIdForUpdate(task.taskId())).thenReturn(Optional.of(task));
        TaskIntakeApplicationService service = service(tasks, premises, scopes, audit, mock(TaskStateTransitionService.class));

        assertThatThrownBy(() -> service.confirmScope(new ConfirmScopeCommand(task.taskId(), "FIN-SYN", "[]",
                "{}", "[]", "[]", "[]", "[]", "[]", "scope-v1", UUID.randomUUID(), NOW)))
                .isInstanceOf(IllegalArgumentException.class);
        org.mockito.Mockito.verify(scopes, org.mockito.Mockito.never()).insert(any());
    }

    @Test
    void taskStateServiceRejectsExecutionWithoutConfirmedScope() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        TaskStateEventRepository events = mock(TaskStateEventRepository.class);
        InvestigationTask task = task(TaskStatus.READY, null, 0);
        when(tasks.findByIdForUpdate(task.taskId())).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> new TaskStateTransitionService(tasks, events, new TaskStateMachine())
                .transition(new TaskStateTransitionCommand(task.taskId(), TaskStatus.WAITING_STEP_APPROVAL,
                        "start", UUID.randomUUID(), NOW, false)))
                .isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(events);
    }

    @Test
    void changingConfirmedScopeMarksOldApprovalsAndDraftsStale() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        TaskPremiseRepository premises = mock(TaskPremiseRepository.class);
        ScopeRevisionRepository scopes = mock(ScopeRevisionRepository.class);
        TaskAuditEventAppender audit = mock(TaskAuditEventAppender.class);
        TaskStateTransitionService state = mock(TaskStateTransitionService.class);
        StepApprovalRepository approvals = mock(StepApprovalRepository.class);
        PacketDraftRepository drafts = mock(PacketDraftRepository.class);
        UUID oldScopeId = UUID.randomUUID();
        InvestigationTask current = task(TaskStatus.READY, oldScopeId, 0);
        InvestigationTask waiting = task(TaskStatus.WAITING_SCOPE_CONFIRMATION, oldScopeId, 1);
        InvestigationTask ready = task(TaskStatus.READY, UUID.randomUUID(), 2);
        when(tasks.findByIdForUpdate(current.taskId())).thenReturn(Optional.of(current));
        when(scopes.findLatestByTaskId(current.taskId())).thenReturn(Optional.of(scope(oldScopeId, current.taskId())));
        when(state.transition(any(TaskStateTransitionCommand.class))).thenReturn(result(waiting), result(ready));
        when(approvals.markStaleByScopeRevision(any(), any(), any(), any())).thenReturn(2);
        when(drafts.markStaleByScopeRevision(any(), any(), any(), any())).thenReturn(1);
        TaskIntakeApplicationService service = new TaskIntakeApplicationService(tasks, premises, scopes, state, audit,
                approvals, drafts);

        service.confirmScope(new ConfirmScopeCommand(current.taskId(), "FIN-SYN", "{\"module\":\"approval\"}",
                "{}", "[]", "[]", "[]", "[]", "[]", "scope-v1", UUID.randomUUID(), NOW));

        verify(approvals).markStaleByScopeRevision(eq(current.taskId()), eq(oldScopeId), any(UUID.class), eq(NOW));
        verify(drafts).markStaleByScopeRevision(eq(current.taskId()), eq(oldScopeId), any(UUID.class), eq(NOW));
    }

    private static TaskIntakeApplicationService service(InvestigationTaskRepository tasks,
            TaskPremiseRepository premises, ScopeRevisionRepository scopes, TaskAuditEventAppender audit,
            TaskStateTransitionService state) {
        return new TaskIntakeApplicationService(tasks, premises, scopes, state, audit);
    }

    private static TaskStateTransitionResult result(InvestigationTask task) {
        TaskStateEvent event = new TaskStateEvent(UUID.randomUUID(), task.taskId(), TaskStatus.DRAFT, task.status(),
                "scope", UUID.randomUUID(), NOW, task.rowVersion());
        return new TaskStateTransitionResult(task, event);
    }

    private static InvestigationTask task(TaskStatus status, UUID scopeRevisionId, long rowVersion) {
        return new InvestigationTask(UUID.randomUUID(), "title", "question", UUID.randomUUID(), status,
                UUID.randomUUID(), scopeRevisionId, null, NOW, NOW, rowVersion, null, null, UUID.randomUUID());
    }

    private static ScopeRevision scope(UUID scopeRevisionId, UUID taskId) {
        return new ScopeRevision(scopeRevisionId, taskId, 1, "FIN-SYN", new cn.com.crc.ems.finance.agent.task.domain.VersionedJson("scope-v1", "{}"),
                new cn.com.crc.ems.finance.agent.task.domain.VersionedJson("scope-v1", "{}"),
                new cn.com.crc.ems.finance.agent.task.domain.VersionedJson("scope-v1", "[]"),
                new cn.com.crc.ems.finance.agent.task.domain.VersionedJson("scope-v1", "[]"),
                new cn.com.crc.ems.finance.agent.task.domain.VersionedJson("scope-v1", "[]"),
                new cn.com.crc.ems.finance.agent.task.domain.VersionedJson("scope-v1", "[]"),
                new cn.com.crc.ems.finance.agent.task.domain.VersionedJson("scope-v1", "[]"), UUID.randomUUID(), NOW,
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "scope-v1", UUID.randomUUID(), NOW,
                UUID.randomUUID(), NOW);
    }
}
