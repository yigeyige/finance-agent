package cn.com.crc.ems.finance.agent.tool.application;

import java.util.Objects;

import io.agentscope.core.permission.PermissionMode;

/**
 *
 * @Description: 在工具请求处暂停 Harness，且明确禁止 Harness permission 绕过业务审批。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class HarnessPermissionMiddleware {

    private final PermissionMode mode;

    public HarnessPermissionMiddleware(PermissionMode mode) {
        this.mode = Objects.requireNonNull(mode, "permission mode is required");
    }

    /** 任何工具请求先进入 HITL；BYPASS/DONT_ASK 也不能直接放行到业务工具。 */
    public HarnessPermissionDecision check(String toolCode) {
        if (toolCode == null || toolCode.isBlank()) {
            throw new IllegalArgumentException("toolCode must not be blank");
        }
        return mode == PermissionMode.BYPASS || mode == PermissionMode.DONT_ASK
                ? HarnessPermissionDecision.DENIED : HarnessPermissionDecision.WAITING_HITL;
    }

    public PermissionMode mode() {
        return mode;
    }
}
