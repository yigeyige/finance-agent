package cn.com.crc.ems.finance.agent.scenario.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;

/** 对外返回的业务场景 Skill 视图。 */
public record BusinessSceneSkillResponse(UUID skillId, String name, String description, String handlingType,
        String premiseType, Instant updatedAt) {

    /** 将领域对象转换为前端需要的安全视图。 */
    public static BusinessSceneSkillResponse from(BusinessSceneSkill skill) {
        return new BusinessSceneSkillResponse(skill.skillId(), skill.name(), skill.description(), skill.handlingType(),
                skill.premiseType(), skill.updatedAt());
    }
}
