package cn.com.crc.ems.finance.agent.task.application;

import java.util.Optional;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;

/**
 *
 * @Description: 步骤审批应用结果，permit 只在服务端内部生成而不出现在结果中。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:30:00
 */
public record StepApprovalResult(InvestigationTask task, StepApproval approval, ApprovalDecision decision) {

    public StepApprovalResult {
        if (task == null || decision == null) {
            throw new IllegalArgumentException("task and decision are required");
        }
        if (decision == ApprovalDecision.APPROVED && approval == null) {
            throw new IllegalArgumentException("approved result requires approval");
        }
    }

    public Optional<StepApproval> approvalOptional() {
        return Optional.ofNullable(approval);
    }
}
