package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import cn.com.crc.ems.finance.agent.tool.application.ToolCatalog;

/**
 *
 * @Description: 管理 Skill 生命周期、权限边界、发布加载和任务版本冻结。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:20:00
 */
public final class SkillCatalogService {

    private final ToolCatalog toolCatalog;
    private final Set<String> allowedFieldCategories;
    private final Clock clock;
    private final Map<SkillKey, SkillPackage> packages = new ConcurrentHashMap<>();
    private final Map<SkillKey, FrozenSkillVersion> frozenVersions = new ConcurrentHashMap<>();
    private final List<SkillApprovalEvent> approvalEvents = new ArrayList<>();

    public SkillCatalogService(ToolCatalog toolCatalog, Set<String> allowedFieldCategories) {
        this(toolCatalog, allowedFieldCategories, Clock.systemUTC());
    }

    public SkillCatalogService(ToolCatalog toolCatalog, Set<String> allowedFieldCategories, Clock clock) {
        this.toolCatalog = Objects.requireNonNull(toolCatalog, "tool catalog is required");
        this.allowedFieldCategories = Set.copyOf(Objects.requireNonNull(allowedFieldCategories,
                "field categories are required"));
        this.clock = Objects.requireNonNull(clock, "clock is required");
    }

    /**
     *
     * @description: 注册只能处于 DRAFT 的 Skill，并校验其权限是现有策略的子集。
     *
     * @param: skill 待注册 Skill。
     */
    public void register(SkillPackage skill) {
        Objects.requireNonNull(skill, "skill is required");
        if (skill.status() != SkillStatus.DRAFT) {
            throw new IllegalArgumentException("only draft skills can be registered");
        }
        validatePermissions(skill);
        SkillKey key = new SkillKey(skill.skillId(), skill.version());
        if (packages.putIfAbsent(key, skill) != null) {
            throw new IllegalStateException("skill version already exists");
        }
    }

    /** 将 DRAFT 标记为已测试，并记录测试操作者。 */
    public SkillPackage markTested(UUID skillId, String version, String evidenceDigest, UUID tester) {
        SkillPackage current = require(skillId, version);
        if (current.status() != SkillStatus.DRAFT || !current.testEvidenceDigest().equals(evidenceDigest)
                || tester == null) {
            throw new IllegalStateException("skill test evidence or lifecycle is invalid");
        }
        return transition(current, SkillStatus.TESTED, tester);
    }

    /** 发布已测试 Skill，发布必须由审批人明确确认。 */
    public SkillPackage publish(UUID skillId, String version, UUID approver) {
        SkillPackage current = require(skillId, version);
        return transition(current, SkillStatus.PUBLISHED, approver);
    }

    /** 退役已发布 Skill，退役后不再允许加载。 */
    public SkillPackage retire(UUID skillId, String version, UUID approver) {
        SkillPackage current = require(skillId, version);
        return transition(current, SkillStatus.RETIRED, approver);
    }

    /** 只返回状态为 PUBLISHED 且内容摘要匹配的 Skill。 */
    public SkillPackage loadPublished(UUID skillId, String version, String expectedDigest) {
        SkillPackage skill = require(skillId, version);
        if (skill.status() != SkillStatus.PUBLISHED || !skill.contentDigest().equals(expectedDigest)) {
            throw new IllegalArgumentException("skill is not a matching published version");
        }
        validatePermissions(skill);
        return skill;
    }

    /** 冻结任务的 Skill 版本；同一任务再次请求不同绑定时拒绝漂移。 */
    public FrozenSkillVersion freezeForTask(UUID taskId, UUID runtimeBundleId, UUID skillId, String version,
            String expectedDigest) {
        if (taskId == null || runtimeBundleId == null) {
            throw new IllegalArgumentException("task and runtime bundle are required");
        }
        SkillPackage skill = loadPublished(skillId, version, expectedDigest);
        SkillKey key = new SkillKey(taskId, skillId.toString());
        FrozenSkillVersion candidate = new FrozenSkillVersion(taskId, runtimeBundleId, skill.skillId(), skill.version(),
                skill.contentDigest(), Instant.now(clock));
        FrozenSkillVersion existing = frozenVersions.putIfAbsent(key, candidate);
        if (existing != null && (!existing.runtimeBundleId().equals(runtimeBundleId)
                || !existing.skillVersion().equals(version) || !existing.contentDigest().equals(expectedDigest))) {
            throw new IllegalStateException("task skill version is already frozen");
        }
        return existing == null ? candidate : existing;
    }

    public Optional<FrozenSkillVersion> frozenForTask(UUID taskId, UUID skillId) {
        if (taskId == null || skillId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(frozenVersions.get(new SkillKey(taskId, skillId.toString())));
    }

    public List<SkillApprovalEvent> approvalEvents() {
        synchronized (approvalEvents) {
            return List.copyOf(approvalEvents);
        }
    }

    public List<SkillPackage> current() {
        return List.copyOf(packages.values());
    }

    private SkillPackage transition(SkillPackage current, SkillStatus target, UUID actor) {
        SkillPackage updated = current.transition(target, actor, Instant.now(clock));
        SkillKey key = new SkillKey(current.skillId(), current.version());
        packages.put(key, updated);
        synchronized (approvalEvents) {
            approvalEvents.add(new SkillApprovalEvent(current.skillId(), current.version(), current.status(), target,
                    actor, updated.updatedAt(), updated.contentDigest()));
        }
        return updated;
    }

    private SkillPackage require(UUID skillId, String version) {
        if (skillId == null || version == null || version.isBlank()) {
            throw new IllegalArgumentException("skill identity is required");
        }
        SkillPackage skill = packages.get(new SkillKey(skillId, version));
        if (skill == null) {
            throw new IllegalArgumentException("skill version is unknown");
        }
        return skill;
    }

    private void validatePermissions(SkillPackage skill) {
        Set<String> catalogTools = toolCatalog.tools().stream().map(tool -> tool.toolCode()).collect(java.util.stream.Collectors.toSet());
        if (!catalogTools.containsAll(skill.allowedTools()) || !allowedFieldCategories.containsAll(skill.allowedFieldCategories())) {
            throw new IllegalArgumentException("skill permissions exceed authoritative policy");
        }
    }

    private record SkillKey(UUID first, String second) {
    }
}
