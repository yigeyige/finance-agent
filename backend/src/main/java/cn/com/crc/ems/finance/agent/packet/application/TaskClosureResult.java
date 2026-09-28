package cn.com.crc.ems.finance.agent.packet.application;

import java.time.Instant;

import cn.com.crc.ems.finance.agent.packet.domain.Handoff;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/** 关闭后的任务结果；关闭不表示整改已完成。 */
public record TaskClosureResult(InvestigationTask task, Handoff handoff, Instant closedAt,
        boolean remediationComplete) {
    public TaskClosureResult {
        if (task == null || handoff == null || closedAt == null || remediationComplete) {
            throw new IllegalArgumentException("task closure result is invalid");
        }
    }
}
