package cn.com.crc.ems.finance.agent.packet.application;

import cn.com.crc.ems.finance.agent.packet.domain.Handoff;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/** 交接后的任务和交接记录。 */
public record PacketHandoffResult(InvestigationTask task, Handoff handoff) {
    public PacketHandoffResult {
        if (task == null || handoff == null) {
            throw new IllegalArgumentException("handoff result is incomplete");
        }
    }
}
