package cn.com.crc.ems.finance.agent.packet.application;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.packet.domain.PacketDraft;

/**
 *
 * @Description: 决策包草稿组装所需的结构化输入，不接受模型自由文本覆盖事实字段。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PacketAssemblyCommand(UUID taskId, UUID scopeRevisionId, UUID runtimeBundleId,
        String problem, JsonNode premises, JsonNode scope, JsonNode sources, JsonNode facts,
        JsonNode computations, JsonNode professionalJudgments, JsonNode recommendations,
        JsonNode businessConfirmations, JsonNode validationPreparation, JsonNode handoff, UUID actorUserId) {
}
