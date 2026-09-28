package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 受控 Subagent 调用的结果、有效权限和公开事件。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:40:00
 */
public record SubagentInvocationOutcome(SubagentResult result, SubagentPermission effectivePermission,
        SubagentEvent event) {
}
