package cn.com.crc.ems.finance.agent.evidence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.evidence.application.ComputationWitnessRepository;
import cn.com.crc.ems.finance.agent.evidence.application.CoverageRecordFactory;
import cn.com.crc.ems.finance.agent.evidence.application.CoverageRecordRepository;
import cn.com.crc.ems.finance.agent.evidence.application.FactSnapshotRepository;
import cn.com.crc.ems.finance.agent.evidence.application.FindingEvidenceRepository;
import cn.com.crc.ems.finance.agent.evidence.application.FindingRepository;
import cn.com.crc.ems.finance.agent.evidence.application.SyntheticEvidenceWorkerService;
import cn.com.crc.ems.finance.agent.rules.application.RuleEvaluationService;
import cn.com.crc.ems.finance.agent.source.infrastructure.SyntheticConfigSourceAdapter;
import cn.com.crc.ems.finance.agent.task.application.InvestigationStepRepository;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.StepApprovalRepository;
import cn.com.crc.ems.finance.agent.task.application.StepExecutionGuard;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationService;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationStartResult;
import cn.com.crc.ems.finance.agent.task.domain.ApprovalStatus;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskLeaseService;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;

/**
 *
 * @Description: 验证受控合成证据 Worker 在审批和幂等边界上的失败关闭与重放行为。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-30 23:30:00
 */
class SyntheticEvidenceWorkerServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-30T15:30:00Z");
    private static final UUID TASK_ID = UUID.randomUUID();
    private static final UUID STEP_ID = UUID.randomUUID();
    private static final UUID ACTOR_ID = UUID.randomUUID();

    /**
     *
     * @description: 当前步骤缺少 ACTIVE 审批时，在创建调用意图或领取租约前拒绝执行。
     *
     * @author: yigeyige99
     */
    @Test
    void rejectsUnapprovedStepBeforeCreatingInvocationOrLease() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        StepApprovalRepository approvals = mock(StepApprovalRepository.class);
        ToolInvocationService invocations = mock(ToolInvocationService.class);
        TaskLeaseService leases = mock(TaskLeaseService.class);
        when(tasks.findById(TASK_ID)).thenReturn(Optional.of(task()));
        when(approvals.findByStepId(STEP_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> service(leases, tasks, approvals, invocations).run(TASK_ID, ACTOR_ID,
                "synthetic-worker-key-0001")).isInstanceOf(IllegalStateException.class)
                .hasMessage("current step has no active approval");

        verifyNoInteractions(invocations, leases);
    }

    /**
     *
     * @description: 已成功的同一幂等键只返回既有结果，不再领取步骤或写入证据链。
     *
     * @author: yigeyige99
     */
    @Test
    void replaysSuccessfulInvocationWithoutClaimingLeaseOrWritingEvidence() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        StepApprovalRepository approvals = mock(StepApprovalRepository.class);
        ToolInvocationService invocations = mock(ToolInvocationService.class);
        TaskLeaseService leases = mock(TaskLeaseService.class);
        StepApproval approval = approval();
        ToolInvocation invocation = new ToolInvocation(UUID.randomUUID(), TASK_ID, STEP_ID, approval.approvalId(),
                "synthetic-evidence", "existing-key", "a".repeat(64), "b".repeat(64), InvocationStatus.SUCCEEDED,
                NOW, NOW, null, ACTOR_ID, NOW, ACTOR_ID, NOW);
        when(tasks.findById(TASK_ID)).thenReturn(Optional.of(task()));
        when(approvals.findByStepId(STEP_ID)).thenReturn(List.of(approval));
        when(invocations.startIntent(org.mockito.ArgumentMatchers.any())).thenReturn(
                new ToolInvocationStartResult(ToolInvocationStartResult.Action.REPLAY, invocation));

        var result = service(leases, tasks, approvals, invocations).run(TASK_ID, ACTOR_ID,
                "synthetic-worker-key-0002");

        assertThat(result.replayed()).isTrue();
        assertThat(result.snapshotCount()).isZero();
        assertThat(result.findingId()).isNull();
        assertThat(result.resultDigest()).isEqualTo("b".repeat(64));
        verifyNoInteractions(leases);
    }

    private static SyntheticEvidenceWorkerService service(TaskLeaseService leases, InvestigationTaskRepository tasks,
            StepApprovalRepository approvals, ToolInvocationService invocations) {
        return new SyntheticEvidenceWorkerService(leases, tasks, mock(InvestigationStepRepository.class), approvals,
                mock(StepExecutionGuard.class), invocations, mock(SyntheticConfigSourceAdapter.class),
                mock(FactSnapshotRepository.class), mock(CoverageRecordRepository.class), mock(FindingRepository.class),
                mock(FindingEvidenceRepository.class), mock(ComputationWitnessRepository.class),
                mock(CoverageRecordFactory.class), mock(RuleEvaluationService.class), mock(TaskStateTransitionService.class),
                mock(TaskAuditEventAppender.class), Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private static InvestigationTask task() {
        return new InvestigationTask(TASK_ID, "合成验证", "检查配置漂移", ACTOR_ID, TaskStatus.WAITING_STEP_APPROVAL,
                UUID.randomUUID(), null, UUID.randomUUID(), STEP_ID, NOW, NOW, 1, null, ACTOR_ID, ACTOR_ID);
    }

    private static StepApproval approval() {
        return new StepApproval(UUID.randomUUID(), STEP_ID, 1, ACTOR_ID, NOW, NOW.plusSeconds(300), "a".repeat(64),
                UUID.randomUUID(), UUID.randomUUID(), "fields-v1", "query-v1", 1, 0, "b".repeat(64),
                ApprovalStatus.ACTIVE, ACTOR_ID, NOW, ACTOR_ID, NOW);
    }
}
