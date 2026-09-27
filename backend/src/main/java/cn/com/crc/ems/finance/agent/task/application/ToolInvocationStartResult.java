package cn.com.crc.ems.finance.agent.task.application;

import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;

/** 幂等开始请求的确定分类，调用方据此决定是否真正执行工具。 */
public record ToolInvocationStartResult(Action action, ToolInvocation invocation) {

    public enum Action {
        EXECUTE,
        REPLAY,
        IN_PROGRESS,
        MANUAL_RECONCILIATION
    }

    public ToolInvocationStartResult {
        if (action == null || invocation == null) {
            throw new IllegalArgumentException("action and invocation are required");
        }
    }
}
