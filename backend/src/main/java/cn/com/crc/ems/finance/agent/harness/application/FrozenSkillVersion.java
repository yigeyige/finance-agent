package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 任务与运行版本绑定的 Skill 冻结记录，防止运行中版本漂移。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:20:00
 */
public record FrozenSkillVersion(UUID taskId, UUID runtimeBundleId, UUID skillId,
        String skillVersion, String contentDigest, Instant frozenAt) {

    public FrozenSkillVersion {
        if (taskId == null || runtimeBundleId == null || skillId == null || skillVersion == null
                || skillVersion.isBlank() || contentDigest == null || !contentDigest.matches("[0-9a-f]{64}")
                || frozenAt == null) {
            throw new IllegalArgumentException("frozen skill version is invalid");
        }
    }
}
