package cn.com.crc.ems.finance.agent.packet.application;

import java.util.UUID;

/** 关闭已交接调查任务的命令。 */
public record TaskClosureCommand(UUID taskId, String closureReason, UUID actorUserId) {

    public TaskClosureCommand {
        if (taskId == null || actorUserId == null || closureReason == null || closureReason.isBlank()) {
            throw new IllegalArgumentException("closure command is incomplete");
        }
    }
}
