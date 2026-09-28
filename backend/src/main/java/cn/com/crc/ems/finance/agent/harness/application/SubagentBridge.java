package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @Description: 受控 Subagent 调用桥，执行权限交集、结果归属和公开事件校验。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:40:00
 */
public final class SubagentBridge {

    private final SubagentCatalog catalog;
    private final Map<String, SubagentHandler> handlers;
    private final Clock clock;

    public SubagentBridge(SubagentCatalog catalog) {
        this(catalog, Map.of("evidence-organizer", new SyntheticSubagentHandler(),
                "plan-drafter", new SyntheticSubagentHandler(), "review-assistant", new SyntheticSubagentHandler()),
                Clock.systemUTC());
    }

    public SubagentBridge(SubagentCatalog catalog, Map<String, SubagentHandler> handlers, Clock clock) {
        this.catalog = Objects.requireNonNull(catalog, "subagent catalog is required");
        this.handlers = Map.copyOf(Objects.requireNonNull(handlers, "subagent handlers are required"));
        this.clock = Objects.requireNonNull(clock, "clock is required");
    }

    /**
     *
     * @description: 执行已注册 Subagent，并将结果包装为主任务可消费的公开完成事件。
     *
     * @param: invocation 调用请求。
     * @return: 结构化结果、有效权限和公开事件。
     * @throws: IllegalArgumentException 角色、权限、发布或范围请求不满足时抛出。
     */
    public SubagentInvocationOutcome invoke(SubagentInvocation invocation) {
        Objects.requireNonNull(invocation, "subagent invocation is required");
        SubagentDescriptor descriptor = catalog.find(invocation.subagentId())
                .orElseThrow(() -> new IllegalArgumentException("subagent role is unknown"));
        if (invocation.publishRequested() || invocation.scopeExpansionRequested()
                || descriptor.canPublish() || descriptor.canExpandScope()) {
            throw new IllegalArgumentException("subagent cannot publish or expand scope");
        }
        SubagentPermission permission = SubagentPermission.intersect(invocation.approvedTools(),
                invocation.approvedFieldCategories(), descriptor);
        if (!permission.allows(invocation.requestedTools(), invocation.requestedFieldCategories())) {
            throw new IllegalArgumentException("subagent permission exceeds task approval");
        }
        SubagentHandler handler = handlers.get(invocation.subagentId());
        if (handler == null) {
            throw new IllegalArgumentException("subagent handler is unavailable");
        }
        SubagentResult result = handler.handle(invocation, permission);
        if (!result.invocationId().equals(invocation.invocationId()) || !result.taskId().equals(invocation.taskId())
                || !result.userId().equals(invocation.userId())) {
            throw new IllegalStateException("subagent result ownership mismatch");
        }
        SubagentEvent event = new SubagentEvent("SUBAGENT_COMPLETED", result.invocationId(), result.taskId(),
                result.userId(), result.resultDigest(), Instant.now(clock));
        return new SubagentInvocationOutcome(result, permission, event);
    }
}
