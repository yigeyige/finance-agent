package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @Description: 主任务审批权限与 Subagent 白名单的交集。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:40:00
 */
public record SubagentPermission(Set<String> effectiveTools, Set<String> effectiveFieldCategories) {

    public SubagentPermission {
        if (effectiveTools == null || effectiveFieldCategories == null) {
            throw new IllegalArgumentException("subagent permission is invalid");
        }
        effectiveTools = Set.copyOf(effectiveTools);
        effectiveFieldCategories = Set.copyOf(effectiveFieldCategories);
    }

    public boolean allows(Set<String> requestedTools, Set<String> requestedFields) {
        return effectiveTools.containsAll(requestedTools) && effectiveFieldCategories.containsAll(requestedFields);
    }

    public static SubagentPermission intersect(Set<String> approvedTools, Set<String> approvedFields,
            SubagentDescriptor descriptor) {
        Set<String> tools = approvedTools.stream().filter(descriptor.allowedTools()::contains).collect(Collectors.toSet());
        Set<String> fields = approvedFields.stream().filter(descriptor.allowedFieldCategories()::contains)
                .collect(Collectors.toSet());
        return new SubagentPermission(tools, fields);
    }
}
