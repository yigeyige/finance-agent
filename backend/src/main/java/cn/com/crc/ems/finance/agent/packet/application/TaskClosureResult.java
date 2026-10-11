package cn.com.crc.ems.finance.agent.packet.application;

import java.time.Instant;

import cn.com.crc.ems.finance.agent.packet.domain.Handoff;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/**
 *
 * @Description: 关闭后的任务结果；关闭不表示整改已完成。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record TaskClosureResult(InvestigationTask task, Handoff handoff, Instant closedAt,
        boolean remediationComplete) {
    public TaskClosureResult {
        if (task == null || handoff == null || closedAt == null || remediationComplete) {
            throw new IllegalArgumentException("task closure result is invalid");
        }
    }
}
