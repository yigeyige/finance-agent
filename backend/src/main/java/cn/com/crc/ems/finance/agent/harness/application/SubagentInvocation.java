package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @Description: 绑定任务和用户的 Subagent 调用请求。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:40:00
 */
public record SubagentInvocation(UUID invocationId, UUID taskId, UUID userId, String subagentId,
        Set<String> approvedTools, Set<String> approvedFieldCategories, Set<String> requestedTools,
        Set<String> requestedFieldCategories, List<String> evidenceRefs, boolean publishRequested,
        boolean scopeExpansionRequested) {

    public SubagentInvocation {
        if (invocationId == null || taskId == null || userId == null || subagentId == null || subagentId.isBlank()
                || approvedTools == null || approvedFieldCategories == null || requestedTools == null
                || requestedFieldCategories == null || evidenceRefs == null || evidenceRefs.isEmpty()
                || evidenceRefs.stream().anyMatch(value -> value == null || value.isBlank())) {
            throw new IllegalArgumentException("subagent invocation is invalid");
        }
        approvedTools = Set.copyOf(approvedTools);
        approvedFieldCategories = Set.copyOf(approvedFieldCategories);
        requestedTools = Set.copyOf(requestedTools);
        requestedFieldCategories = Set.copyOf(requestedFieldCategories);
        evidenceRefs = List.copyOf(evidenceRefs);
    }
}
