package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;

/**
 * @Description: 场景预填的模型输入：目标、描述、当前名称、前置提示词与筛选后的表名。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 17:00:00
 */
public record SceneSkillPrefillContext(SceneSkillPrefillTarget target, String text, String sceneName,
        String prerequisitePrompt, List<String> tableNames) {

    public SceneSkillPrefillContext {
        if (target == null || text == null || text.isBlank()) {
            throw new IllegalArgumentException("scene prefill context is invalid");
        }
        sceneName = sceneName == null ? "" : sceneName.strip();
        prerequisitePrompt = prerequisitePrompt == null ? "" : prerequisitePrompt.strip();
        tableNames = tableNames == null ? List.of() : List.copyOf(tableNames);
    }
}
