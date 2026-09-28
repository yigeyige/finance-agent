package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;

/**
 *
 * @Description: 三类 Subagent 的合成处理器，不访问真实数据、网络、文件或凭证。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:40:00
 */
public final class SyntheticSubagentHandler implements SubagentHandler {

    @Override
    public SubagentResult handle(SubagentInvocation invocation, SubagentPermission permission) {
        String summary = switch (invocation.subagentId()) {
            case "evidence-organizer" -> "synthetic-evidence-links-ready";
            case "plan-drafter" -> "synthetic-plan-draft-ready";
            case "review-assistant" -> "synthetic-review-gaps-ready";
            default -> throw new IllegalArgumentException("subagent role is unknown");
        };
        return SubagentResult.create(invocation.invocationId(), invocation.taskId(), invocation.userId(),
                invocation.subagentId(), summary, List.copyOf(invocation.evidenceRefs()));
    }
}
