package cn.com.crc.ems.finance.agent.packet.application;

import java.util.UUID;

/**
 *
 * @Description: 关闭已交接调查任务的命令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record TaskClosureCommand(UUID taskId, String closureReason, UUID actorUserId) {

    public TaskClosureCommand {
        if (taskId == null || actorUserId == null || closureReason == null || closureReason.isBlank()) {
            throw new IllegalArgumentException("closure command is incomplete");
        }
    }
}
