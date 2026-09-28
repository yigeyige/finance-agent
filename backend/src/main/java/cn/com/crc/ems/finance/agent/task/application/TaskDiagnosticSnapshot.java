package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/** 面向工作台的脱敏任务诊断快照。 */
public record TaskDiagnosticSnapshot(UUID taskId, TaskStatus taskStatus, CurrentStep currentStep,
        LeaseSummary lease, boolean approvalValid, String pauseReason, String lastErrorCode,
        boolean manualReconciliationRequired, List<String> recoveryActions) {

    public TaskDiagnosticSnapshot {
        if (taskId == null || taskStatus == null || lease == null || recoveryActions == null) {
            throw new IllegalArgumentException("task diagnostic snapshot is incomplete");
        }
        recoveryActions = List.copyOf(recoveryActions);
    }

    /** 当前步骤的最小公开摘要。 */
    public record CurrentStep(UUID stepId, int sequenceNo, String goal, String status) {
        public CurrentStep {
            if (stepId == null || sequenceNo < 1 || goal == null || goal.isBlank() || status == null) {
                throw new IllegalArgumentException("current step summary is invalid");
            }
        }
    }

    /** 不包含租约持有者凭据的租约摘要。 */
    public record LeaseSummary(String status, Instant heartbeatAt, Instant expiresAt) {
        public LeaseSummary {
            if (status == null || status.isBlank()) {
                throw new IllegalArgumentException("lease status is required");
            }
        }
    }
}
