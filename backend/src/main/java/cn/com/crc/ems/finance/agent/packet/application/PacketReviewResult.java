package cn.com.crc.ems.finance.agent.packet.application;

import cn.com.crc.ems.finance.agent.packet.domain.PacketDraft;
import cn.com.crc.ems.finance.agent.packet.domain.PacketReview;

/** 草稿复核结果，保留被复核草稿的绑定信息。 */
public record PacketReviewResult(PacketDraft draft, PacketReview review) {
}
