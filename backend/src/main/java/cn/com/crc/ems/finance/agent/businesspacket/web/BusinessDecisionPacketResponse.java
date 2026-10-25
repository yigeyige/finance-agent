package cn.com.crc.ems.finance.agent.businesspacket.web;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.businesspacket.application.BusinessDecisionPacketResult;
import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessDecisionPacket;

/**
 *
 * @Description: 业务决策包查询与生成响应，content 为决策包规范内容对象。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
public record BusinessDecisionPacketResponse(UUID packetId, UUID taskId, int revision, Instant createdAt,
        Object content, boolean created, String message) {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    /** 由已持久化的决策包构造查询响应。 */
    public static BusinessDecisionPacketResponse from(BusinessDecisionPacket packet) {
        return new BusinessDecisionPacketResponse(packet.packetId(), packet.taskId(), packet.revision(),
                packet.createdAt(), parse(packet.canonicalContent()), false, null);
    }

    /** 由生成结果构造响应，区分新增与跳过。 */
    public static BusinessDecisionPacketResponse from(BusinessDecisionPacketResult result) {
        BusinessDecisionPacket packet = result.packet();
        return new BusinessDecisionPacketResponse(packet.packetId(), packet.taskId(), packet.revision(),
                packet.createdAt(), parse(packet.canonicalContent()), result.created(), result.message());
    }

    private static Object parse(String json) {
        try {
            return JSON_MAPPER.readValue(json, Object.class);
        } catch (Exception exception) {
            throw new IllegalStateException("决策包内容不可解析", exception);
        }
    }
}
