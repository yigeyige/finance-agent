package cn.com.crc.ems.finance.agent.packet.application;

import cn.com.crc.ems.finance.agent.packet.domain.PacketDraft;
import cn.com.crc.ems.finance.agent.packet.domain.PacketReview;

/**
 *
 * @Description: 草稿复核结果，保留被复核草稿的绑定信息。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PacketReviewResult(PacketDraft draft, PacketReview review) {
}
