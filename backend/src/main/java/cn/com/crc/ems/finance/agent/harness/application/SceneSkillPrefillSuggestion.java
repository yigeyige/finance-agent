package cn.com.crc.ems.finance.agent.harness.application;

/**
 * @Description: 场景预填结果；调查目标包含场景名称，其余目标名称留空。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 17:00:00
 */
public record SceneSkillPrefillSuggestion(String name, String content) {

    public SceneSkillPrefillSuggestion {
        name = name == null ? "" : name.strip();
        content = content == null ? "" : content.strip();
    }
}
