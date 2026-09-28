package cn.com.crc.ems.finance.agent.packet.application;

import cn.com.crc.ems.finance.agent.packet.domain.PacketDraft;

/** 草稿组装结果，HTML 是规范 JSON 的只读快照。 */
public record PacketDraftAssemblyResult(PacketDraft draft, String htmlSnapshot) {
}
