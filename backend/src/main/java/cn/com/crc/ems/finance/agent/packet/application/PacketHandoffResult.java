package cn.com.crc.ems.finance.agent.packet.application;

import cn.com.crc.ems.finance.agent.packet.domain.Handoff;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/**
 *
 * @Description: 交接后的任务和交接记录。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PacketHandoffResult(InvestigationTask task, Handoff handoff) {
    public PacketHandoffResult {
        if (task == null || handoff == null) {
            throw new IllegalArgumentException("handoff result is incomplete");
        }
    }
}
