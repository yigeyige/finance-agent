package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Set;

/**
 *
 * @Description: 注册 Subagent 角色及其最小工具、字段和禁止能力边界。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:40:00
 */
public record SubagentDescriptor(String id, SubagentRole role, Set<String> allowedTools,
        Set<String> allowedFieldCategories, boolean canPublish, boolean canExpandScope) {

    public SubagentDescriptor {
        if (id == null || id.isBlank() || role == null || allowedTools == null || allowedFieldCategories == null
                || canPublish || canExpandScope) {
            throw new IllegalArgumentException("subagent descriptor is invalid");
        }
        allowedTools = Set.copyOf(allowedTools);
        allowedFieldCategories = Set.copyOf(allowedFieldCategories);
    }
}
