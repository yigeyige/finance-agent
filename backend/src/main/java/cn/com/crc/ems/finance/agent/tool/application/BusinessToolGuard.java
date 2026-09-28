package cn.com.crc.ems.finance.agent.tool.application;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.task.application.ExecutionPermit;
import cn.com.crc.ems.finance.agent.task.application.StepExecutionGuard;

/** 在工具目录校验之外复核当前业务审批并签发短期 permit。 */
@Service
public class BusinessToolGuard {

    private final StepExecutionGuard executionGuard;
    private final ToolCatalogService catalogService;
    private final PermitSigner permitSigner;
    private final Clock applicationClock;

    public BusinessToolGuard(StepExecutionGuard executionGuard, ToolCatalogService catalogService,
            PermitSigner permitSigner, Clock applicationClock) {
        this.executionGuard = executionGuard;
        this.catalogService = catalogService;
        this.permitSigner = permitSigner;
        this.applicationClock = applicationClock;
    }

    public SignedToolPermit issue(ToolGuardRequest request) {
        catalogService.resolve(request.runtimeBundleId(), request.toolCode(), request.toolVersion(),
                request.taskStatus(), request.environment());
        ExecutionPermit permit = executionGuard.issuePermit(request.execution());
        if (!executionGuard.isValid(permit, Instant.now(applicationClock))) {
            throw new IllegalStateException("execution permit is not valid");
        }
        return new SignedToolPermit(permit, request.toolCode(), request.toolVersion(),
                permitSigner.sign(permit, request.toolCode(), request.toolVersion()));
    }

    public void verify(SignedToolPermit permit) {
        if (!permitSigner.verify(permit) || !executionGuard.isValid(permit.permit(), Instant.now(applicationClock))) {
            throw new IllegalStateException("tool permit is expired or tampered");
        }
    }
}
