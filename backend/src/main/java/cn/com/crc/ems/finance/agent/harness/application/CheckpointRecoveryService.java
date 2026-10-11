package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.harness.domain.Checkpoint;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.application.ToolInvocationRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;

/**
 *
 * @Description: 重新验证业务上下文并给出无副作用的 checkpoint 恢复决策。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Service
public class CheckpointRecoveryService {

    private final CheckpointRepository checkpointRepository;
    private final SessionStateRepository sessionStateRepository;
    private final CheckpointBusinessContextRepository businessContextRepository;
    private final TaskLeaseRepository leaseRepository;
    private final ToolInvocationRepository invocationRepository;

    public CheckpointRecoveryService(CheckpointRepository checkpointRepository,
            SessionStateRepository sessionStateRepository, CheckpointBusinessContextRepository businessContextRepository,
            TaskLeaseRepository leaseRepository,
            ToolInvocationRepository invocationRepository) {
        this.checkpointRepository = checkpointRepository;
        this.sessionStateRepository = sessionStateRepository;
        this.businessContextRepository = businessContextRepository;
        this.leaseRepository = leaseRepository;
        this.invocationRepository = invocationRepository;
    }

    /** 只读判定恢复动作，不获取租约、不执行工具、不修改任何状态。 */
    @Transactional(readOnly = true)
    public CheckpointRecoveryResult decide(CheckpointRecoveryRequest request) {
        Checkpoint checkpoint = checkpointRepository.findById(request.checkpointId(), request.taskId(), request.userId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("checkpoint not found"));
        if (!request.sessionId().equals(checkpoint.sessionId())) {
            return result(CheckpointRecoveryDecision.NEW_APPROVAL_REQUIRED, checkpoint, null,
                    "checkpoint session binding is stale");
        }
        var context = businessContextRepository.find(checkpoint.taskId(), checkpoint.stepId(), checkpoint.scopeRevisionId(),
                checkpoint.approvalId()).orElseThrow(() -> new RepositoryEntityNotFoundException("task context not found"));
        if (!request.userId().equals(checkpoint.userId()) || checkpoint.runtimeBundleId() == null
                || !checkpoint.runtimeBundleId().equals(context.runtimeBundleId())) {
            return result(CheckpointRecoveryDecision.NEW_APPROVAL_REQUIRED, checkpoint, null,
                    "checkpoint runtime binding is stale");
        }
        if (!request.taskId().equals(checkpoint.taskId()) || !checkpoint.stepId().equals(context.currentStepId())
                || !checkpoint.scopeRevisionId().equals(context.currentScopeRevisionId())) {
            return result(CheckpointRecoveryDecision.NEW_APPROVAL_REQUIRED, checkpoint, null,
                    "checkpoint step or scope binding is stale");
        }
        if (sessionStateRepository.findById(checkpoint.sessionId(), checkpoint.taskId(), checkpoint.userId()).isEmpty()
                || !context.stepBindingAvailable() || !context.scopeBindingAvailable()) {
            return result(CheckpointRecoveryDecision.NEW_APPROVAL_REQUIRED, checkpoint, null,
                    "checkpoint business binding is unavailable");
        }
        if (!approvalIsCurrent(context, checkpoint, request.observedAt())) {
            return result(CheckpointRecoveryDecision.NEW_APPROVAL_REQUIRED, checkpoint, null,
                    "approval is stale, revoked, expired or exhausted");
        }

        Optional<TaskLease> activeLease = leaseRepository.findActiveByTaskId(checkpoint.taskId());
        if (activeLease.filter(lease -> !lease.isExpiredAt(request.observedAt()))
                .filter(lease -> !checkpoint.stepId().equals(lease.stepId())
                        || request.ownerInstance() == null
                        || !request.ownerInstance().equals(lease.ownerInstance())).isPresent()) {
            return result(CheckpointRecoveryDecision.MANUAL_RECONCILIATION, checkpoint, null,
                    "another active worker lease owns the checkpoint step");
        }

        ToolInvocation invocation = findInvocation(request.idempotencyKey());
        if (invocation == null) {
            if ("INVOCATION_INTENT_RECORDED".equals(checkpoint.checkpointType())) {
                return result(CheckpointRecoveryDecision.MANUAL_RECONCILIATION, checkpoint, null,
                        "invocation checkpoint has no persisted invocation");
            }
            return result(CheckpointRecoveryDecision.RESUME, checkpoint, null, "checkpoint is safe to resume");
        }
        return invocationDecision(checkpoint, invocation);
    }

    private ToolInvocation findInvocation(String idempotencyKey) {
        return idempotencyKey == null ? null : invocationRepository.findByIdempotencyKey(idempotencyKey).orElse(null);
    }

    private static CheckpointRecoveryResult invocationDecision(Checkpoint checkpoint, ToolInvocation invocation) {
        return switch (invocation.status()) {
            case SUCCEEDED, PARTIAL -> result(CheckpointRecoveryDecision.REPLAY_RESULT, checkpoint, invocation,
                    "persisted invocation result can be replayed");
            case FAILED, INTENT_RECORDED -> result(CheckpointRecoveryDecision.RESUME, checkpoint, invocation,
                    "definite invocation failure or intent can resume through guards");
            case RUNNING, UNKNOWN -> result(CheckpointRecoveryDecision.MANUAL_RECONCILIATION, checkpoint, invocation,
                    "invocation may have produced an external side effect");
        };
    }

    private static boolean approvalIsCurrent(CheckpointBusinessContextRepository.CheckpointBusinessContext context,
            Checkpoint checkpoint, Instant observedAt) {
        return context.approvalBindingAvailable() && context.approvalActive()
                && (context.approvalExpiresAt() == null || context.approvalExpiresAt().isAfter(observedAt))
                && context.approvalUsedCalls() < context.approvalMaxCalls();
    }

    private static CheckpointRecoveryResult result(CheckpointRecoveryDecision decision, Checkpoint checkpoint,
            ToolInvocation invocation, String reason) {
        return new CheckpointRecoveryResult(decision, checkpoint, invocation, reason);
    }
}
