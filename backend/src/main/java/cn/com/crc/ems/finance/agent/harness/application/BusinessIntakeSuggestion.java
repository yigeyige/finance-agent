package cn.com.crc.ems.finance.agent.harness.application;

/**
 * @Description: 模型对自由文本的解析结果；场景与实例为候选名称，空字符串表示模型判断不需要。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:00:00
 */
public record BusinessIntakeSuggestion(String title, String description, String sceneSkillName,
        String databaseName) {

    public BusinessIntakeSuggestion {
        title = title == null ? "" : title.strip();
        description = description == null ? "" : description.strip();
        sceneSkillName = sceneSkillName == null ? "" : sceneSkillName.strip();
        databaseName = databaseName == null ? "" : databaseName.strip();
    }
}
