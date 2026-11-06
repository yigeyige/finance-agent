package cn.com.crc.ems.finance.agent.harness.application;

import java.util.UUID;

/**
 * @Description: AI 预填接口结果：已匹配的场景/实例标识与名称，未匹配为空。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:00:00
 */
public record BusinessIntakeDraft(String title, String description, UUID sceneSkillId,
        String sceneSkillName, UUID databaseConfigId, String databaseName) {
}
