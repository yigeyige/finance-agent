package cn.com.crc.ems.finance.agent.scenario.domain;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @Description: 可复用业务处理场景的受控领域对象。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-08 23:00:00
 */
public record BusinessSceneSkill(UUID skillId, String name, String description, String handlingType, String premiseType,
        UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {

    private static final Set<String> HANDLING_TYPES = Set.of("INVESTIGATE", "ADJUST");

    public BusinessSceneSkill {
        if (skillId == null || createdBy == null || createdAt == null || updatedBy == null || updatedAt == null) {
            throw new IllegalArgumentException("business scene skill audit fields are required");
        }
        validateText(name, 100, "name");
        validateText(description, 1000, "description");
        validateText(premiseType, 100, "premiseType");
        if (!HANDLING_TYPES.contains(handlingType)) {
            throw new IllegalArgumentException("handlingType is unsupported");
        }
    }

    /**
     *
     * @description: 创建由当前管理员维护的业务场景 Skill。
     *
     * @param: skillId Skill 标识
     * @param: name 业务名称
     * @param: description 业务说明
     * @param: handlingType 处理方式
     * @param: premiseType 业务场景
     * @param: operatorId 当前管理员标识
     * @param: now 当前时间
     * @return: 可持久化的业务场景 Skill
     * @author: yigeyige99
     */
    public static BusinessSceneSkill create(UUID skillId, String name, String description, String handlingType,
            String premiseType, UUID operatorId, Instant now) {
        return new BusinessSceneSkill(skillId, name, description, handlingType, premiseType, operatorId, now,
                operatorId, now);
    }

    /**
     *
     * @description: 生成保留创建审计字段的已编辑 Skill。
     *
     * @param: name 业务名称
     * @param: description 业务说明
     * @param: handlingType 处理方式
     * @param: premiseType 业务场景
     * @param: operatorId 当前管理员标识
     * @param: now 编辑时间
     * @return: 已更新的业务场景 Skill
     * @author: yigeyige99
     */
    public BusinessSceneSkill revise(String name, String description, String handlingType, String premiseType,
            UUID operatorId, Instant now) {
        return new BusinessSceneSkill(skillId, name, description, handlingType, premiseType, createdBy, createdAt,
                operatorId, now);
    }

    private static void validateText(String value, int maxLength, String field) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
