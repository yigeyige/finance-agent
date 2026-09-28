package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: Skill 生命周期变更的可追溯审批记录。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:20:00
 */
public record SkillApprovalEvent(UUID skillId, String skillVersion, SkillStatus fromStatus,
        SkillStatus toStatus, UUID actorId, Instant occurredAt, String contentDigest) {

    public SkillApprovalEvent {
        if (skillId == null || skillVersion == null || skillVersion.isBlank() || fromStatus == null
                || toStatus == null || actorId == null || occurredAt == null || contentDigest == null
                || !contentDigest.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("skill approval event is invalid");
        }
    }
}
