package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 受控 Subagent 的纯业务处理入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:40:00
 */
@FunctionalInterface
public interface SubagentHandler {

    /** 只使用请求中已批准的引用和权限生成结构化结果。 */
    SubagentResult handle(SubagentInvocation invocation, SubagentPermission permission);
}
