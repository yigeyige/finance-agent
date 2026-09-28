package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @Description: 首期固定 Subagent 目录，不支持运行时动态注册或增权。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:40:00
 */
public final class SubagentCatalog {

    private final Map<String, SubagentDescriptor> descriptors;

    public SubagentCatalog(List<SubagentDescriptor> descriptors) {
        if (descriptors == null || descriptors.isEmpty()) {
            throw new IllegalArgumentException("subagent catalog is empty");
        }
        this.descriptors = descriptors.stream().collect(Collectors.toUnmodifiableMap(SubagentDescriptor::id,
                Function.identity()));
    }

    public static SubagentCatalog defaultCatalog() {
        return new SubagentCatalog(List.of(
                new SubagentDescriptor("evidence-organizer", SubagentRole.EVIDENCE_ORGANIZER,
                        Set.of("read-config"), Set.of("SAFE_EVIDENCE"), false, false),
                new SubagentDescriptor("plan-drafter", SubagentRole.PLAN_DRAFTER, Set.of(),
                        Set.of("SAFE_EVIDENCE", "PLAN_METADATA"), false, false),
                new SubagentDescriptor("review-assistant", SubagentRole.REVIEW_ASSISTANT, Set.of(),
                        Set.of("SAFE_EVIDENCE", "DRAFT_SUMMARY"), false, false)));
    }

    public Optional<SubagentDescriptor> find(String id) {
        return Optional.ofNullable(descriptors.get(id));
    }

    public List<SubagentDescriptor> all() {
        return List.copyOf(descriptors.values());
    }
}
