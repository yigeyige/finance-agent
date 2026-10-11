package cn.com.crc.ems.finance.agent.packet.application;

import cn.com.crc.ems.finance.agent.packet.domain.PacketDraft;

/**
 *
 * @Description: 草稿组装结果，HTML 是规范 JSON 的只读快照。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PacketDraftAssemblyResult(PacketDraft draft, String htmlSnapshot) {
}
