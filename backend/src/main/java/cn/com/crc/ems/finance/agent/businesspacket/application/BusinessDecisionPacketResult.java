package cn.com.crc.ems.finance.agent.businesspacket.application;

import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessDecisionPacket;

/**
 *
 * @Description: 业务决策包生成结果，created 标识是否新增修订，跳过时返回既有最新修订。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
public record BusinessDecisionPacketResult(BusinessDecisionPacket packet, boolean created, String message) {
}
