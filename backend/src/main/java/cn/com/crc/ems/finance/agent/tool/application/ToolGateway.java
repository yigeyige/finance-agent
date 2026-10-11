package cn.com.crc.ems.finance.agent.tool.application;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.task.application.ToolInvocationService;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationStartResult;

/**
 *
 * @Description: 双层守卫后的工具调用管线；只在守卫通过后记录幂等调用意图。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Service
public class ToolGateway {

    private final BusinessToolGuard businessGuard;
    private final ToolInvocationService invocationService;

    public ToolGateway(BusinessToolGuard businessGuard, ToolInvocationService invocationService) {
        this.businessGuard = businessGuard;
        this.invocationService = invocationService;
    }

    public SignedToolPermit authorize(ToolGuardRequest request) {
        return businessGuard.issue(request);
    }

    public ToolInvocationStartResult recordIntent(SignedToolPermit permit,
            cn.com.crc.ems.finance.agent.task.application.ToolInvocationStartCommand command) {
        businessGuard.verify(permit);
        if (!permit.permit().idempotencyKey().equals(command.key().value())
                || !permit.permit().approvalId().equals(command.approvalId())
                || !permit.toolCode().equals(command.toolCode())) {
            throw new IllegalArgumentException("tool permit does not match invocation intent");
        }
        return invocationService.startIntent(command);
    }
}
