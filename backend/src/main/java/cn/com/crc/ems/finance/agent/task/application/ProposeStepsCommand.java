package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 *
 * @Description: 追加一个推荐步骤和有限备选步骤的应用命令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:30:00
 */
public record ProposeStepsCommand(UUID taskId, List<StepProposalItem> steps, UUID actorUserId,
        Instant occurredAt) {

    public ProposeStepsCommand {
        if (taskId == null || actorUserId == null || occurredAt == null || steps == null) {
            throw new IllegalArgumentException("taskId, steps, actorUserId and occurredAt are required");
        }
        steps = List.copyOf(steps);
    }
}
