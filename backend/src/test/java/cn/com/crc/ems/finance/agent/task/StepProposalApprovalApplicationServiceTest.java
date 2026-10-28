package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.application.ApprovalDecision;
import cn.com.crc.ems.finance.agent.task.application.CreateStepApprovalCommand;
import cn.com.crc.ems.finance.agent.task.application.ExecutionPermit;
import cn.com.crc.ems.finance.agent.task.application.InvestigationStepRepository;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.ProposeStepsCommand;
import cn.com.crc.ems.finance.agent.task.application.ReviseStepProposalCommand;
import cn.com.crc.ems.finance.agent.task.application.StepApprovalApplicationService;
import cn.com.crc.ems.finance.agent.task.application.StepApprovalRepository;
import cn.com.crc.ems.finance.agent.task.application.StepExecutionGuard;
import cn.com.crc.ems.finance.agent.task.application.StepExecutionRequest;
import cn.com.crc.ems.finance.agent.task.application.StepProposalApplicationService;
import cn.com.crc.ems.finance.agent.task.application.StepProposalItem;
import cn.com.crc.ems.finance.agent.task.domain.ApprovalStatus;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;
import cn.com.crc.ems.finance.agent.task.domain.StepStatus;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateEvent;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;

/** 验证步骤提议、审批 revision 和内部 permit 守卫。 */
class StepProposalApprovalApplicationServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-27T03:00:00Z");
    private static final UUID ACTOR = UUID.randomUUID();

    @Test
    void rejectsMoreThanTwoAlternativesBeforeWriting() {
        var service = new StepProposalApplicationService(mock(InvestigationTaskRepository.class),
                mock(InvestigationStepRepository.class), mock(TaskStateTransitionService.class),
                mock(TaskAuditEventAppender.class), clock());

        assertThatThrownBy(() -> service.propose(new ProposeStepsCommand(UUID.randomUUID(), List.of(
                new StepProposalItem("a", true), new StepProposalItem("b", false),
                new StepProposalItem("c", false), new StepProposalItem("d", false)), ACTOR, NOW)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void storesRecommendationAndAlternativesAndTransitionsTask() {
        var tasks = mock(InvestigationTaskRepository.class);
        var steps = mock(InvestigationStepRepository.class);
        var state = mock(TaskStateTransitionService.class);
        var audit = mock(TaskAuditEventAppender.class);
        UUID scopeId = UUID.randomUUID();
        InvestigationTask ready = task(TaskStatus.READY, scopeId, null, 2);
        InvestigationTask waiting = task(TaskStatus.WAITING_STEP_APPROVAL, scopeId, UUID.randomUUID(), 3);
        when(tasks.findByIdForUpdate(ready.taskId())).thenReturn(Optional.of(ready));
        when(steps.findByTaskId(ready.taskId())).thenReturn(List.of());
        when(state.transition(any())).thenReturn(new cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionResult(
                waiting, new TaskStateEvent(UUID.randomUUID(), waiting.taskId(), TaskStatus.READY,
                        TaskStatus.WAITING_STEP_APPROVAL, "proposal", ACTOR, NOW, waiting.rowVersion())));
        var service = new StepProposalApplicationService(tasks, steps, state, audit, clock());

        var result = service.propose(new ProposeStepsCommand(ready.taskId(), List.of(
                new StepProposalItem("recommended", true), new StepProposalItem("alternative", false)), ACTOR, NOW));

        assertThat(result.steps()).hasSize(2);
        assertThat(result.steps().get(0).status()).isEqualTo(StepStatus.PROPOSED);
        assertThat(result.task().status()).isEqualTo(TaskStatus.WAITING_STEP_APPROVAL);
        verify(steps, org.mockito.Mockito.times(2)).insert(any());
        verify(state).transition(any());
    }

    @Test
    void revisesOnlyCurrentProposedRecommendation() {
        var tasks = mock(InvestigationTaskRepository.class);
        var steps = mock(InvestigationStepRepository.class);
        UUID scopeId = UUID.randomUUID();
        UUID stepId = UUID.randomUUID();
        InvestigationTask waiting = task(TaskStatus.WAITING_STEP_APPROVAL, scopeId, stepId, 3);
        InvestigationStep proposed = step(stepId, waiting.taskId(), "old goal");
        when(tasks.findByIdForUpdate(waiting.taskId())).thenReturn(Optional.of(waiting));
        when(steps.findById(stepId)).thenReturn(Optional.of(proposed));
        when(steps.updateProposedGoal(stepId, "new goal", ACTOR, NOW)).thenReturn(1);

        InvestigationStep revised = new StepProposalApplicationService(tasks, steps,
                mock(TaskStateTransitionService.class), mock(TaskAuditEventAppender.class), clock())
                .revise(new ReviseStepProposalCommand(waiting.taskId(), stepId, "new goal", ACTOR, NOW));

        assertThat(revised.goal()).isEqualTo("new goal");
        verify(steps).updateProposedGoal(stepId, "new goal", ACTOR, NOW);
    }

    @Test
    void rejectsRevisionOutsideWaitingProposalState() {
        var tasks = mock(InvestigationTaskRepository.class);
        UUID stepId = UUID.randomUUID();
        InvestigationTask running = task(TaskStatus.RUNNING_STEP, UUID.randomUUID(), stepId, 3);
        when(tasks.findByIdForUpdate(running.taskId())).thenReturn(Optional.of(running));
        var service = new StepProposalApplicationService(tasks, mock(InvestigationStepRepository.class),
                mock(TaskStateTransitionService.class), mock(TaskAuditEventAppender.class), clock());

        assertThatThrownBy(() -> service.revise(new ReviseStepProposalCommand(running.taskId(), stepId, "new goal", ACTOR, NOW)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void reusesIdenticalActiveApprovalAndCreatesRevisionWhenBindingChanges() {
        var tasks = mock(InvestigationTaskRepository.class);
        var steps = mock(InvestigationStepRepository.class);
        var approvals = mock(StepApprovalRepository.class);
        var audit = mock(TaskAuditEventAppender.class);
        UUID scopeId = UUID.randomUUID();
        UUID stepId = UUID.randomUUID();
        InvestigationTask task = task(TaskStatus.WAITING_STEP_APPROVAL, scopeId, stepId, 2);
        InvestigationStep step = step(stepId, task.taskId(), "read config");
        when(tasks.findByIdForUpdate(task.taskId())).thenReturn(Optional.of(task));
        when(steps.findById(stepId)).thenReturn(Optional.of(step));
        when(approvals.findByStepId(stepId)).thenReturn(List.of());
        var service = new StepApprovalApplicationService(tasks, steps, approvals, audit, clock());
        String goalHash = StepExecutionGuard.goalHash(step.goal());
        CreateStepApprovalCommand command = approvalCommand(task, step, scopeId, goalHash, "field-v1", 2);

        var first = service.approve(command).approval();
        when(approvals.findByStepId(stepId)).thenReturn(List.of(first));
        var reused = service.approve(command).approval();
        var changed = service.approve(approvalCommand(task, step, scopeId, goalHash, "field-v2", 2)).approval();

        assertThat(reused.approvalId()).isEqualTo(first.approvalId());
        assertThat(changed.revisionNo()).isEqualTo(2);
        verify(approvals, org.mockito.Mockito.times(2)).insert(any());
    }

    @Test
    void guardRejectsExpiredApprovalAndFieldPolicyDrift() {
        var tasks = mock(InvestigationTaskRepository.class);
        var steps = mock(InvestigationStepRepository.class);
        var approvals = mock(StepApprovalRepository.class);
        UUID scopeId = UUID.randomUUID();
        UUID stepId = UUID.randomUUID();
        UUID approvalId = UUID.randomUUID();
        InvestigationTask task = task(TaskStatus.RUNNING_STEP, scopeId, stepId, 3);
        InvestigationStep step = step(stepId, task.taskId(), "read config");
        StepApproval expired = approval(approvalId, step, scopeId, ApprovalStatus.ACTIVE, 0, 1,
                NOW.minusSeconds(1), "field-v1", "query-v1");
        when(tasks.findById(task.taskId())).thenReturn(Optional.of(task));
        when(steps.findById(stepId)).thenReturn(Optional.of(step));
        when(approvals.findById(approvalId)).thenReturn(Optional.of(expired));
        var guard = new StepExecutionGuard(tasks, steps, approvals, clock());
        StepExecutionRequest request = request(task, step, approvalId, scopeId, "field-v1", "query-v1");

        assertThatThrownBy(() -> guard.issuePermit(request)).isInstanceOf(IllegalStateException.class)
                .hasMessage("approval has expired");
        StepApproval valid = approval(approvalId, step, scopeId, ApprovalStatus.ACTIVE, 0, 1,
                NOW.plusSeconds(600), "field-v1", "query-v1");
        when(approvals.findById(approvalId)).thenReturn(Optional.of(valid));
        assertThatThrownBy(() -> guard.issuePermit(new StepExecutionRequest(task.taskId(), stepId, approvalId,
                scopeId, valid.sourceBindingId(), StepExecutionGuard.goalHash(step.goal()), "field-v2", "query-v1",
                "idempotency-key-0001", NOW))).isInstanceOf(IllegalStateException.class)
                .hasMessage("approval binding drifted");
    }

    @Test
    void guardRejectsRevokedOldScopeAndExhaustedApproval() {
        var tasks = mock(InvestigationTaskRepository.class);
        var steps = mock(InvestigationStepRepository.class);
        var approvals = mock(StepApprovalRepository.class);
        UUID scopeId = UUID.randomUUID();
        UUID stepId = UUID.randomUUID();
        UUID approvalId = UUID.randomUUID();
        InvestigationTask task = task(TaskStatus.RUNNING_STEP, scopeId, stepId, 3);
        InvestigationStep step = step(stepId, task.taskId(), "read config");
        when(tasks.findById(task.taskId())).thenReturn(Optional.of(task));
        when(steps.findById(stepId)).thenReturn(Optional.of(step));
        var guard = new StepExecutionGuard(tasks, steps, approvals, clock());

        for (ApprovalStatus status : List.of(ApprovalStatus.REVOKED, ApprovalStatus.STALE)) {
            StepApproval approval = approval(approvalId, step, scopeId, status, 0, 1,
                    NOW.plusSeconds(600), "field-v1", "query-v1");
            when(approvals.findById(approvalId)).thenReturn(Optional.of(approval));
            assertThatThrownBy(() -> guard.issuePermit(request(task, step, approvalId, scopeId, "field-v1", "query-v1")))
                    .isInstanceOf(IllegalStateException.class).hasMessage("approval is not active");
        }
        StepApproval exhausted = approval(approvalId, step, scopeId, ApprovalStatus.ACTIVE, 1, 1,
                NOW.plusSeconds(600), "field-v1", "query-v1");
        when(approvals.findById(approvalId)).thenReturn(Optional.of(exhausted));
        assertThatThrownBy(() -> guard.issuePermit(request(task, step, approvalId, scopeId, "field-v1", "query-v1")))
                .isInstanceOf(IllegalStateException.class).hasMessage("approval call limit is exhausted");
    }

    @Test
    void validGuardIssuesPermitWithShortLifetime() {
        var tasks = mock(InvestigationTaskRepository.class);
        var steps = mock(InvestigationStepRepository.class);
        var approvals = mock(StepApprovalRepository.class);
        UUID scopeId = UUID.randomUUID();
        UUID stepId = UUID.randomUUID();
        UUID approvalId = UUID.randomUUID();
        InvestigationTask task = task(TaskStatus.RUNNING_STEP, scopeId, stepId, 3);
        InvestigationStep step = step(stepId, task.taskId(), "read config");
        StepApproval approval = approval(approvalId, step, scopeId, ApprovalStatus.ACTIVE, 0, 2,
                NOW.plusSeconds(600), "field-v1", "query-v1");
        when(tasks.findById(task.taskId())).thenReturn(Optional.of(task));
        when(steps.findById(stepId)).thenReturn(Optional.of(step));
        when(approvals.findById(approvalId)).thenReturn(Optional.of(approval));
        ExecutionPermit permit = new StepExecutionGuard(tasks, steps, approvals, clock())
                .issuePermit(request(task, step, approvalId, scopeId, "field-v1", "query-v1"));

        assertThat(permit.approvalDigest()).isEqualTo(approval.approvalDigest());
        assertThat(permit.expiresAt()).isBeforeOrEqualTo(NOW.plusSeconds(600));
    }

    private static CreateStepApprovalCommand approvalCommand(InvestigationTask task, InvestigationStep step,
            UUID scopeId, String goalHash, String fieldPolicy, int maxCalls) {
        return new CreateStepApprovalCommand(task.taskId(), step.stepId(), goalHash, UUID.randomUUID(), scopeId,
                fieldPolicy, "query-v1", maxCalls, NOW.plusSeconds(600), task.rowVersion(), ApprovalDecision.APPROVED,
                ACTOR, NOW);
    }

    private static StepExecutionRequest request(InvestigationTask task, InvestigationStep step, UUID approvalId,
            UUID scopeId, String fieldPolicy, String queryTemplate) {
        return new StepExecutionRequest(task.taskId(), step.stepId(), approvalId, scopeId, task.taskId(),
                StepExecutionGuard.goalHash(step.goal()), fieldPolicy, queryTemplate, "idempotency-key-0001", NOW);
    }

    private static StepApproval approval(UUID approvalId, InvestigationStep step, UUID scopeId, ApprovalStatus status,
            int usedCalls, int maxCalls, Instant expiresAt, String fieldPolicy, String queryTemplate) {
        return new StepApproval(approvalId, step.stepId(), 1, ACTOR, NOW, expiresAt,
                StepExecutionGuard.goalHash(step.goal()), step.taskId(), scopeId, fieldPolicy, queryTemplate,
                maxCalls, usedCalls, "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb", status,
                ACTOR, NOW, ACTOR, NOW);
    }

    private static InvestigationStep step(UUID stepId, UUID taskId, String goal) {
        return new InvestigationStep(stepId, taskId, 1, goal, true, StepStatus.PROPOSED, NOW, null, ACTOR, NOW, ACTOR);
    }

    private static InvestigationTask task(TaskStatus status, UUID scopeId, UUID stepId, long rowVersion) {
        return new InvestigationTask(UUID.randomUUID(), "title", "question", UUID.randomUUID(), status,
                UUID.randomUUID(), null, scopeId, stepId, NOW, NOW, rowVersion, null, ACTOR, ACTOR);
    }

    private static Clock clock() {
        return Clock.fixed(NOW, ZoneOffset.UTC);
    }
}
