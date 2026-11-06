package cn.com.crc.ems.finance.agent.scenario.web;

import cn.com.crc.ems.finance.agent.scenario.application.SceneSkillPrefillResult;

/**
 * @Description: 场景预填响应；调查目标返回 name，其余目标 name 为空。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 17:30:00
 */
public record SceneSkillPrefillResponse(String target, String name, String content, int tableCount,
        int excludedTableCount, String warning) {

    public static SceneSkillPrefillResponse from(SceneSkillPrefillResult result) {
        return new SceneSkillPrefillResponse(result.target().name(), result.name(), result.content(),
                result.tableCount(), result.excludedTableCount(), result.warning());
    }
}
