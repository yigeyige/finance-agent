package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.application.InvestigationStepRepository;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.StepApprovalRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskDiagnosticService;
import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;

/** 验证诊断状态映射和敏感错误摘要脱敏。 */
class TaskDiagnosticServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-29T00:00:00Z");
    private static final UUID TASK_ID = UUID.randomUUID();

    @Test
    void redactsUnsafeLastErrorAndExposesRecoverySummary() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        ToolInvocationRepository invocations = mock(ToolInvocationRepository.class);
        when(tasks.findById(TASK_ID)).thenReturn(Optional.of(task(TaskStatus.RUNNING_STEP)));
        when(invocations.findLatestByTaskId(TASK_ID)).thenReturn(Optional.of(invocation(
                "select password=secret from finance_user")));

        var snapshot = service(tasks, invocations).diagnose(TASK_ID);

        assertThat(snapshot.lastErrorCode()).isEqualTo("REDACTED");
        assertThat(snapshot.manualReconciliationRequired()).isFalse();
        assertThat(snapshot.recoveryActions()).containsExactly("暂无可执行动作");
    }

    @Test
    void mapsUnknownInvocationToManualReconciliation() {
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        ToolInvocationRepository invocations = mock(ToolInvocationRepository.class);
        when(tasks.findById(TASK_ID)).thenReturn(Optional.of(task(TaskStatus.RUNNING_STEP)));
        when(invocations.findLatestByTaskId(TASK_ID)).thenReturn(Optional.of(invocation(null)));

        var snapshot = service(tasks, invocations).diagnose(TASK_ID);

        assertThat(snapshot.lastErrorCode()).isEqualTo("MANUAL_RECONCILIATION_REQUIRED");
        assertThat(snapshot.manualReconciliationRequired()).isTrue();
        assertThat(snapshot.recoveryActions()).containsExactly("人工对账");
    }

    private static TaskDiagnosticService service(InvestigationTaskRepository tasks,
            ToolInvocationRepository invocations) {
        return new TaskDiagnosticService(tasks, mock(InvestigationStepRepository.class), mock(TaskLeaseRepository.class),
                mock(StepApprovalRepository.class), invocations, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private static InvestigationTask task(TaskStatus status) {
        return new InvestigationTask(TASK_ID, "title", "question", UUID.randomUUID(), status, UUID.randomUUID(),
                null, UUID.randomUUID(), null, NOW, NOW, 1, null, UUID.randomUUID(), UUID.randomUUID());
    }

    private static ToolInvocation invocation(String errorCode) {
        return new ToolInvocation(UUID.randomUUID(), TASK_ID, UUID.randomUUID(), UUID.randomUUID(), "tool",
                "idempotency-key", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                null, errorCode == null ? InvocationStatus.UNKNOWN : InvocationStatus.FAILED, NOW, null, errorCode,
                UUID.randomUUID(), NOW, UUID.randomUUID(), NOW);
    }
}
