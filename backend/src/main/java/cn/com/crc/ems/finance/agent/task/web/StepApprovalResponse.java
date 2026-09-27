package cn.com.crc.ems.finance.agent.task.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.application.StepApprovalResult;

/**
 *
 * @Description: 步骤审批最小响应，不暴露内部 ExecutionPermit。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:45:00
 */
public record StepApprovalResponse(UUID taskId, UUID stepId, UUID approvalId, Integer revisionNo,
        String status, String approvalDigest, Instant expiresAt, long rowVersion, String decision) {

    public static StepApprovalResponse from(StepApprovalResult result) {
        var approval = result.approval();
        return new StepApprovalResponse(result.task().taskId(), approval == null ? null : approval.stepId(),
                approval == null ? null : approval.approvalId(), approval == null ? null : approval.revisionNo(),
                approval == null ? "REJECTED" : approval.status().name(),
                approval == null ? null : approval.approvalDigest(), approval == null ? null : approval.expiresAt(),
                result.task().rowVersion(), result.decision().name());
    }
}
