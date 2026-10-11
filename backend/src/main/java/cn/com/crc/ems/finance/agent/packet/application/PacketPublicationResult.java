package cn.com.crc.ems.finance.agent.packet.application;

import cn.com.crc.ems.finance.agent.packet.domain.PacketRevision;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 *
 * @Description: 发布前重查和发布结果，stale 时不返回修订。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PacketPublicationResult(PacketRevision revision, ScopeRecheckResult recheck,
        boolean published, TaskStatus taskStatus) {
}
