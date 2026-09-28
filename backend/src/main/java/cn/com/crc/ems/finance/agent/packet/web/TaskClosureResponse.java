package cn.com.crc.ems.finance.agent.packet.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.application.TaskClosureResult;

/** 调查关闭响应，明确关闭不是整改完成。 */
public record TaskClosureResponse(UUID taskId, String taskStatus, Instant closedAt,
        UUID handoffId, boolean remediationComplete) {

    public static TaskClosureResponse from(TaskClosureResult result) {
        return new TaskClosureResponse(result.task().taskId(), result.task().status().name(), result.closedAt(),
                result.handoff().handoffId(), result.remediationComplete());
    }
}
