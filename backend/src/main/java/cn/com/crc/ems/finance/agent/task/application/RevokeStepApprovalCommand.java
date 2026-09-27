package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 撤销活动步骤审批的应用命令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:30:00
 */
public record RevokeStepApprovalCommand(UUID taskId, UUID approvalId, UUID actorUserId, Instant occurredAt) {

    public RevokeStepApprovalCommand {
        if (taskId == null || approvalId == null || actorUserId == null || occurredAt == null) {
            throw new IllegalArgumentException("taskId, approvalId, actorUserId and occurredAt are required");
        }
    }
}
