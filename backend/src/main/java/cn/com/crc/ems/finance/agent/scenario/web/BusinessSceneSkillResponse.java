package cn.com.crc.ems.finance.agent.scenario.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;

/**
 *
 * @Description: 对外返回的业务场景 Skill 视图。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-27 11:20:00
 */
public record BusinessSceneSkillResponse(UUID skillId, String name, String investigationPrompt,
        String decisionPacketPrompt, Instant updatedAt) {

    /** 将领域对象转换为前端需要的安全视图。 */
    public static BusinessSceneSkillResponse from(BusinessSceneSkill skill) {
        return new BusinessSceneSkillResponse(skill.skillId(), skill.name(), skill.investigationPrompt(),
                skill.decisionPacketPrompt(), skill.updatedAt());
    }
}
