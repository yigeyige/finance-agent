package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;

/**
 * @Description: AI 预填的模型输入：自由文本与可选的业务场景/数据库实例候选。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:00:00
 */
public record BusinessIntakeContext(String text, List<BusinessIntakeOption> sceneSkills,
        List<BusinessIntakeOption> databaseInstances) {

    public BusinessIntakeContext {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("business intake text is required");
        }
        sceneSkills = sceneSkills == null ? List.of() : List.copyOf(sceneSkills);
        databaseInstances = databaseInstances == null ? List.of() : List.copyOf(databaseInstances);
    }
}
