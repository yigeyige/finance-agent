package cn.com.crc.ems.finance.agent.businessintake.web;

import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeDraft;

/**
 * @Description: AI 预填响应；未匹配的场景/实例返回 null，不回显模型建议名称。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:30:00
 */
public record BusinessIntakeDraftResponse(String title, String description, String sceneSkillId,
        String sceneSkillName, String databaseConfigId, String databaseName) {

    public static BusinessIntakeDraftResponse from(BusinessIntakeDraft draft) {
        return new BusinessIntakeDraftResponse(draft.title(), draft.description(),
                draft.sceneSkillId() == null ? null : draft.sceneSkillId().toString(), draft.sceneSkillName(),
                draft.databaseConfigId() == null ? null : draft.databaseConfigId().toString(), draft.databaseName());
    }
}
