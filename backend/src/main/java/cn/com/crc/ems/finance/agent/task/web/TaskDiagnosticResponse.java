package cn.com.crc.ems.finance.agent.task.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.application.TaskDiagnosticSnapshot;

/**
 *
 * @Description: 脱敏任务诊断 REST 响应。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record TaskDiagnosticResponse(UUID taskId, String taskStatus, CurrentStepResponse currentStep,
        LeaseResponse lease, boolean approvalValid, String pauseReason, String lastErrorCode,
        boolean manualReconciliationRequired, List<String> recoveryActions) {

    public static TaskDiagnosticResponse from(TaskDiagnosticSnapshot snapshot) {
        var step = snapshot.currentStep();
        var lease = snapshot.lease();
        return new TaskDiagnosticResponse(snapshot.taskId(), snapshot.taskStatus().name(),
                step == null ? null : new CurrentStepResponse(step.stepId(), step.sequenceNo(), step.goal(), step.status()),
                new LeaseResponse(lease.status(), lease.heartbeatAt(), lease.expiresAt()), snapshot.approvalValid(),
                snapshot.pauseReason(), snapshot.lastErrorCode(), snapshot.manualReconciliationRequired(),
                snapshot.recoveryActions());
    }

    /** 当前步骤的安全摘要。 */
    public record CurrentStepResponse(UUID stepId, int sequenceNo, String goal, String status) {
    }

    /** 租约状态和时间，不返回持有者实例。 */
    public record LeaseResponse(String status, Instant heartbeatAt, Instant expiresAt) {
    }
}
