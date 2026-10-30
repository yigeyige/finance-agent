package cn.com.crc.ems.finance.agent.scenario.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 可复用业务处理场景的受控领域对象，仅保留场景名称与调查/决策包两类提示词。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-27 11:20:00
 */
public record BusinessSceneSkill(UUID skillId, String name, String investigationPrompt, String decisionPacketPrompt,
        UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public BusinessSceneSkill {
        if (skillId == null || createdBy == null || createdAt == null || updatedBy == null || updatedAt == null) {
            throw new IllegalArgumentException("business scene skill audit fields are required");
        }
        validateText(name, 100, "name");
        validateText(investigationPrompt, 2000, "investigationPrompt");
        validateOptionalText(decisionPacketPrompt, 2000, "decisionPacketPrompt");
    }

    /**
     *
     * @description: 创建由当前管理员维护的业务场景 Skill。
     *
     * @param: skillId Skill 标识
     * @param: name 场景名称
     * @param: investigationPrompt 调查提示词
     * @param: decisionPacketPrompt 决策包提示词
     * @param: operatorId 当前管理员标识
     * @param: now 当前时间
     * @return: 可持久化的业务场景 Skill
     * @author: yigeyige99
     */
    public static BusinessSceneSkill create(UUID skillId, String name, String investigationPrompt,
            String decisionPacketPrompt, UUID operatorId, Instant now) {
        return new BusinessSceneSkill(skillId, name, investigationPrompt, decisionPacketPrompt, operatorId, now,
                operatorId, now);
    }

    /**
     *
     * @description: 生成保留创建审计字段的已编辑 Skill。
     *
     * @param: name 场景名称
     * @param: investigationPrompt 调查提示词
     * @param: decisionPacketPrompt 决策包提示词
     * @param: operatorId 当前管理员标识
     * @param: now 编辑时间
     * @return: 已更新的业务场景 Skill
     * @author: yigeyige99
     */
    public BusinessSceneSkill revise(String name, String investigationPrompt, String decisionPacketPrompt,
            UUID operatorId, Instant now) {
        return new BusinessSceneSkill(skillId, name, investigationPrompt, decisionPacketPrompt, createdBy, createdAt,
                operatorId, now);
    }

    private static void validateText(String value, int maxLength, String field) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }

    /** 校验可选文本：允许为空，非空时仅限制长度。 */
    private static void validateOptionalText(String value, int maxLength, String field) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
