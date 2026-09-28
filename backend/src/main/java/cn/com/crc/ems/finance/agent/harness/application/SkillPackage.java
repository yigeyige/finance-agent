package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/**
 *
 * @Description: 不可变 Skill 包元数据及其工具、字段权限声明。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:20:00
 */
public record SkillPackage(UUID skillId, String name, String version, Set<String> taskTypes,
        String inputSchemaRef, String outputSchemaRef, String content, String contentDigest,
        String testEvidenceDigest, Set<String> allowedTools, Set<String> allowedFieldCategories,
        SkillStatus status, UUID approvedBy, Instant createdAt, Instant updatedAt) {

    public SkillPackage {
        if (skillId == null || name == null || name.isBlank() || version == null || version.isBlank()
                || taskTypes == null || taskTypes.isEmpty() || inputSchemaRef == null || inputSchemaRef.isBlank()
                || outputSchemaRef == null || outputSchemaRef.isBlank() || content == null
                || contentDigest == null || !contentDigest.matches("[0-9a-f]{64}")
                || !Sha256Digest.hex(content).equals(contentDigest) || testEvidenceDigest == null
                || !testEvidenceDigest.matches("[0-9a-f]{64}") || allowedTools == null
                || allowedFieldCategories == null || status == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("skill package is invalid");
        }
        taskTypes = Set.copyOf(taskTypes);
        allowedTools = Set.copyOf(allowedTools);
        allowedFieldCategories = Set.copyOf(allowedFieldCategories);
        if ((status == SkillStatus.PUBLISHED || status == SkillStatus.RETIRED) && approvedBy == null) {
            throw new IllegalArgumentException("published or retired skill requires approver");
        }
    }

    /**
     *
     * @description: 生成带新生命周期状态和审批人的不可变 Skill 修订。
     *
     * @param: nextStatus 目标状态。
     * @param: approver 审批或测试操作者。
     * @param: at 状态变更时间。
     * @return: 新状态 Skill 包。
     */
    public SkillPackage transition(SkillStatus nextStatus, UUID approver, Instant at) {
        if (nextStatus == null || at == null || !legalTransition(status, nextStatus)) {
            throw new IllegalStateException("skill lifecycle transition is not allowed");
        }
        if ((nextStatus == SkillStatus.PUBLISHED || nextStatus == SkillStatus.RETIRED) && approver == null) {
            throw new IllegalArgumentException("skill approval is required");
        }
        return new SkillPackage(skillId, name, version, taskTypes, inputSchemaRef, outputSchemaRef, content,
                contentDigest, testEvidenceDigest, allowedTools, allowedFieldCategories, nextStatus,
                approver == null ? approvedBy : approver, createdAt, at);
    }

    private static boolean legalTransition(SkillStatus current, SkillStatus next) {
        return (current == SkillStatus.DRAFT && next == SkillStatus.TESTED)
                || (current == SkillStatus.TESTED && next == SkillStatus.PUBLISHED)
                || (current == SkillStatus.PUBLISHED && next == SkillStatus.RETIRED);
    }
}
