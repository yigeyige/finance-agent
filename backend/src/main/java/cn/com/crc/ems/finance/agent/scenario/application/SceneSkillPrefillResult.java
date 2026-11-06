package cn.com.crc.ems.finance.agent.scenario.application;

import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillTarget;

/**
 * @Description: 场景预填结果；tableCount/excludedTableCount 用于前端提示筛选效果。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 17:10:00
 */
public record SceneSkillPrefillResult(SceneSkillPrefillTarget target, String name, String content,
        int tableCount, int excludedTableCount, String warning) {
}
