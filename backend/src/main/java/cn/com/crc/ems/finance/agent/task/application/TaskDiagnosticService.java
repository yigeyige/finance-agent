package cn.com.crc.ems.finance.agent.task.application;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.domain.ApprovalStatus;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;

/** 在只读事务中汇总任务运行状态，并严格限制诊断输出字段。 */
@Service
public class TaskDiagnosticService {

    private static final Pattern SAFE_ERROR_CODE = Pattern.compile("[A-Z0-9][A-Z0-9_-]{0,99}");
    private final InvestigationTaskRepository taskRepository;
    private final InvestigationStepRepository stepRepository;
    private final TaskLeaseRepository leaseRepository;
    private final StepApprovalRepository approvalRepository;
    private final ToolInvocationRepository invocationRepository;
    private final Clock applicationClock;

    public TaskDiagnosticService(InvestigationTaskRepository taskRepository,
            InvestigationStepRepository stepRepository, TaskLeaseRepository leaseRepository,
            StepApprovalRepository approvalRepository, ToolInvocationRepository invocationRepository,
            Clock applicationClock) {
        this.taskRepository = taskRepository;
        this.stepRepository = stepRepository;
        this.leaseRepository = leaseRepository;
        this.approvalRepository = approvalRepository;
        this.invocationRepository = invocationRepository;
        this.applicationClock = applicationClock;
    }

    /** 查询任务诊断摘要，不返回原始异常或敏感运行内容。 */
    @Transactional(readOnly = true)
    public TaskDiagnosticSnapshot diagnose(UUID taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        InvestigationTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        Instant now = Instant.now(applicationClock);
        InvestigationStep step = task.currentStepId() == null ? null
                : stepRepository.findById(task.currentStepId()).orElse(null);
        TaskLease lease = leaseRepository.findActiveByTaskId(taskId).orElse(null);
        StepApproval approval = step == null ? null : latestApproval(step.stepId());
        ToolInvocation invocation = invocationRepository.findLatestByTaskId(taskId).orElse(null);
        boolean manualReconciliation = invocation != null && invocation.status() == InvocationStatus.UNKNOWN;
        String lastErrorCode = manualReconciliation ? "MANUAL_RECONCILIATION_REQUIRED"
                : safeErrorCode(invocation == null ? null : invocation.errorCode());
        if (lastErrorCode == null) {
            lastErrorCode = pauseErrorCode(task.status());
        }
        return new TaskDiagnosticSnapshot(task.taskId(), task.status(), step == null ? null
                : new TaskDiagnosticSnapshot.CurrentStep(step.stepId(), step.sequenceNo(), step.goal(), step.status().name()),
                leaseSummary(lease, now), approvalValid(approval, now), pauseReason(task.status()), lastErrorCode,
                manualReconciliation, recoveryActions(task.status(), lease, manualReconciliation, now));
    }

    private StepApproval latestApproval(UUID stepId) {
        return approvalRepository.findByStepId(stepId).stream()
                .max(java.util.Comparator.comparingInt(StepApproval::revisionNo)).orElse(null);
    }

    private static TaskDiagnosticSnapshot.LeaseSummary leaseSummary(TaskLease lease, Instant now) {
        if (lease == null) {
            return new TaskDiagnosticSnapshot.LeaseSummary("NONE", null, null);
        }
        return new TaskDiagnosticSnapshot.LeaseSummary(lease.isExpiredAt(now) ? "EXPIRED" : "ACTIVE",
                lease.heartbeatAt(), lease.expiresAt());
    }

    private static boolean approvalValid(StepApproval approval, Instant now) {
        return approval != null && approval.status() == ApprovalStatus.ACTIVE
                && (approval.expiresAt() == null || approval.expiresAt().isAfter(now));
    }

    private static String pauseReason(TaskStatus status) {
        return switch (status) {
            case PAUSED_MODEL_UNAVAILABLE -> "模型不可用";
            case PAUSED_SOURCE_UNAVAILABLE -> "来源不可用";
            case PAUSED_BUDGET -> "预算已耗尽";
            case FAILED_RECOVERABLE -> "可恢复失败";
            default -> null;
        };
    }

    private static String pauseErrorCode(TaskStatus status) {
        return switch (status) {
            case PAUSED_MODEL_UNAVAILABLE -> "MODEL_UNAVAILABLE";
            case PAUSED_SOURCE_UNAVAILABLE -> "SOURCE_UNAVAILABLE";
            case PAUSED_BUDGET -> "BUDGET_EXHAUSTED";
            case FAILED_RECOVERABLE -> "RECOVERABLE_FAILURE";
            default -> null;
        };
    }

    private static List<String> recoveryActions(TaskStatus status, TaskLease lease, boolean manualReconciliation,
            Instant now) {
        if (manualReconciliation) {
            return List.of("人工对账");
        }
        if (status == TaskStatus.PAUSED_MODEL_UNAVAILABLE || status == TaskStatus.PAUSED_SOURCE_UNAVAILABLE
                || status == TaskStatus.PAUSED_BUDGET || status == TaskStatus.FAILED_RECOVERABLE) {
            return List.of("检查暂停原因后恢复");
        }
        if (lease != null && lease.isExpiredAt(now)) {
            return List.of("重新领取租约");
        }
        if (status == TaskStatus.WAITING_STEP_APPROVAL || status == TaskStatus.WAITING_PROFESSIONAL_CONFIRMATION
                || status == TaskStatus.WAITING_REVIEW || status == TaskStatus.WAITING_SCOPE_CONFIRMATION) {
            return List.of("等待人工处理");
        }
        return List.of("暂无可执行动作");
    }

    private static String safeErrorCode(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toUpperCase(java.util.Locale.ROOT);
        return SAFE_ERROR_CODE.matcher(normalized).matches() ? normalized : "REDACTED";
    }
}
