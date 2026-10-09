package cn.com.crc.ems.finance.agent.task.web;

import java.time.Clock;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.com.crc.ems.finance.agent.shared.domain.UtcTime;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.task.application.ApprovalDecision;
import cn.com.crc.ems.finance.agent.task.application.CreateStepApprovalCommand;
import cn.com.crc.ems.finance.agent.task.application.ProposeStepsCommand;
import cn.com.crc.ems.finance.agent.task.application.RevokeStepApprovalCommand;
import cn.com.crc.ems.finance.agent.task.application.ReviseStepProposalCommand;
import cn.com.crc.ems.finance.agent.task.application.StepApprovalApplicationService;
import cn.com.crc.ems.finance.agent.task.application.StepApprovalResult;
import cn.com.crc.ems.finance.agent.task.application.StepProposalApplicationService;

import jakarta.validation.Valid;

/**
 *
 * @Description: 步骤提议、审批和撤销 REST 入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:45:00
 */
@RestController
@RequestMapping("/api/v1/tasks")
public final class StepProposalController {

    private final StepProposalApplicationService proposalService;
    private final StepApprovalApplicationService approvalService;
    private final Clock applicationClock;

    public StepProposalController(StepProposalApplicationService proposalService,
            StepApprovalApplicationService approvalService, Clock applicationClock) {
        this.proposalService = proposalService;
        this.approvalService = approvalService;
        this.applicationClock = applicationClock;
    }

    /** 保存有界步骤提议。 */
    @PostMapping("/{taskId}/step-proposals")
    public ResponseEntity<StepProposalResponse> propose(@PathVariable UUID taskId,
            @Valid @RequestBody StepProposalRequest request, @RequestHeader(value = "X-Actor-User-Id", required = false) UUID actorUserId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        actorUserId = trustedActor(actorUserId);
        var items = request.steps().stream()
                .map(item -> new cn.com.crc.ems.finance.agent.task.application.StepProposalItem(item.goal(),
                        Boolean.TRUE.equals(item.recommended())))
                .toList();
        var result = proposalService.propose(new ProposeStepsCommand(taskId, items, actorUserId,
                UtcTime.now(applicationClock)));
        return ResponseEntity.status(201).body(StepProposalResponse.from(result));
    }

    /** 修改尚未执行的当前推荐方案。 */
    @PatchMapping("/{taskId}/step-proposals/{stepId}")
    public ResponseEntity<StepProposalResponse.StepView> revise(@PathVariable UUID taskId, @PathVariable UUID stepId,
            @Valid @RequestBody StepProposalRevisionRequest request,
            @RequestHeader(value = "X-Actor-User-Id", required = false) UUID actorUserId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        var step = proposalService.revise(new ReviseStepProposalCommand(taskId, stepId, request.goal(), trustedActor(actorUserId),
                UtcTime.now(applicationClock)));
        return ResponseEntity.ok(StepProposalResponse.StepView.from(step));
    }

    /** 保存或复用步骤审批 revision。 */
    @PostMapping("/{taskId}/step-approvals")
    public ResponseEntity<StepApprovalResponse> approve(@PathVariable UUID taskId,
            @Valid @RequestBody StepApprovalRequest request, @RequestHeader(value = "X-Actor-User-Id", required = false) UUID actorUserId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        actorUserId = trustedActor(actorUserId);
        StepApprovalResult result = approvalService.approve(new CreateStepApprovalCommand(taskId, request.stepId(),
                request.goalHash(), request.sourceBindingId(), request.scopeRevisionId(), request.fieldPolicyVersion(),
                request.queryTemplateVersion(), request.maxCalls(), request.expiresAt(), request.expectedRowVersion(),
                request.decision(), actorUserId, UtcTime.now(applicationClock)));
        return ResponseEntity.status(201).body(StepApprovalResponse.from(result));
    }

    /** 撤销活动审批，保留审批历史。 */
    @DeleteMapping("/{taskId}/step-approvals/{approvalId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID taskId, @PathVariable UUID approvalId,
            @RequestHeader(value = "X-Actor-User-Id", required = false) UUID actorUserId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        actorUserId = trustedActor(actorUserId);
        approvalService.revoke(new RevokeStepApprovalCommand(taskId, approvalId, actorUserId,
                UtcTime.now(applicationClock)));
        return ResponseEntity.noContent().build();
    }

    private static void requireIdempotencyKey(String value) {
        if (value == null || value.isBlank() || value.length() < 16 || value.length() > 200) {
            throw new IllegalArgumentException("Idempotency-Key must contain 16 to 200 characters");
        }
    }

    /** 优先使用安全上下文，保留独立控制器测试的旧头兼容路径。 */
    private static UUID trustedActor(UUID legacyActorUserId) {
        return IdentityContext.current() == null ? legacyActorUserId : IdentityContext.require().userId();
    }
}
