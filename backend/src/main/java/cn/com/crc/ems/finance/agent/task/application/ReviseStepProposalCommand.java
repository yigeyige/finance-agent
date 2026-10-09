package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/** 修改尚未执行的推荐业务方案的受限命令。 */
public record ReviseStepProposalCommand(UUID taskId, UUID stepId, String goal, UUID actorUserId, Instant occurredAt) {
    public ReviseStepProposalCommand {
        if (taskId == null || stepId == null || actorUserId == null || occurredAt == null || goal == null
                || goal.isBlank() || goal.length() > 1000) {
            throw new IllegalArgumentException("step proposal revision is invalid");
        }
    }
}
