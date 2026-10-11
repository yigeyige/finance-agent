package cn.com.crc.ems.finance.agent.packet.application;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.domain.Handoff;
import cn.com.crc.ems.finance.agent.packet.domain.PacketRevision;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 *
 * @Description: 面向工作台的已发布决策包只读视图。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PacketPublishedView(UUID taskId, PacketRevision revision, TaskStatus taskStatus,
        Instant closedAt, Handoff handoff, String limitations) {

    public PacketPublishedView {
        if (taskId == null || revision == null || taskStatus == null || limitations == null) {
            throw new IllegalArgumentException("published packet view is incomplete");
        }
    }
}
