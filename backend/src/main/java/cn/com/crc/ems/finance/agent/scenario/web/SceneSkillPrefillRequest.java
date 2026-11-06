package cn.com.crc.ems.finance.agent.scenario.web;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillTarget;
import cn.com.crc.ems.finance.agent.scenario.application.SceneSkillPrefillCommand;

/**
 * @Description: 场景预填请求，只接受白名单字段；target 取 INVESTIGATION/DECISION_PACKET/PROCESS_ARTIFACT。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 17:30:00
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record SceneSkillPrefillRequest(String target, String text, String skillId, String databaseConfigId,
        String sceneName, String investigationPrompt, String decisionPacketPrompt) {

    /** 转换为应用层命令，并把非法枚举/标识转成可读的 400 错误。 */
    public SceneSkillPrefillCommand toCommand() {
        SceneSkillPrefillTarget parsedTarget;
        try {
            parsedTarget = target == null ? null : SceneSkillPrefillTarget.valueOf(target);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("生成目标不合法");
        }
        return new SceneSkillPrefillCommand(parsedTarget, text, parseUuid(skillId), parseUuid(databaseConfigId),
                sceneName, investigationPrompt, decisionPacketPrompt);
    }

    private static UUID parseUuid(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("标识格式不合法");
        }
    }
}
