package cn.com.crc.ems.finance.agent.scenario.application;

import java.util.UUID;

import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillTarget;

/**
 * @Description: 场景预填的应用层入参，controller 解析后传入，避免服务依赖 Web 类型。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 17:10:00
 */
public record SceneSkillPrefillCommand(SceneSkillPrefillTarget target, String text, UUID skillId,
        UUID databaseConfigId, String sceneName, String investigationPrompt, String decisionPacketPrompt) {
}
