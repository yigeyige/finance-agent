package cn.com.crc.ems.finance.agent.task.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.task.domain.ApprovalStatus;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 *
 * @Description: 在外部源调用前复核任务、步骤、范围、审批和版本绑定并签发内部 permit。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:35:00
 */
@Service
public final class StepExecutionGuard {

    private static final Duration PERMIT_TTL = Duration.ofMinutes(5);

    private final InvestigationTaskRepository taskRepository;
    private final InvestigationStepRepository stepRepository;
    private final StepApprovalRepository approvalRepository;
    private final Clock applicationClock;

    public StepExecutionGuard(InvestigationTaskRepository taskRepository,
            InvestigationStepRepository stepRepository, StepApprovalRepository approvalRepository,
            Clock applicationClock) {
        this.taskRepository = taskRepository;
        this.stepRepository = stepRepository;
        this.approvalRepository = approvalRepository;
        this.applicationClock = applicationClock;
    }

    /**
     *
     * @description: 重新读取并校验所有执行边界后签发短期内部 permit。
     *
     * @param: request 外部调用前的绑定请求
     * @return: 仅供服务端内部使用的 permit
     * @throws: IllegalStateException 任一状态、版本或额度校验失败
     */
    public ExecutionPermit issuePermit(StepExecutionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }
        Instant now = Instant.now(applicationClock);
        InvestigationTask task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new IllegalStateException("investigation task not found"));
        if (task.status() != TaskStatus.WAITING_STEP_APPROVAL && task.status() != TaskStatus.RUNNING_STEP) {
            throw new IllegalStateException("task is not in an executable approval state");
        }
        if (!request.stepId().equals(task.currentStepId())
                || !request.scopeRevisionId().equals(task.currentScopeRevisionId())) {
            throw new IllegalStateException("execution binding is not current");
        }
        InvestigationStep step = stepRepository.findById(request.stepId())
                .orElseThrow(() -> new IllegalStateException("investigation step not found"));
        if (!request.taskId().equals(step.taskId())) {
            throw new IllegalStateException("step does not belong to task");
        }
        StepApproval approval = approvalRepository.findById(request.approvalId())
                .orElseThrow(() -> new IllegalStateException("step approval not found"));
        if (!request.stepId().equals(approval.stepId()) || !request.scopeRevisionId().equals(approval.scopeRevisionId())
                || !request.sourceBindingId().equals(approval.sourceBindingId())
                || !request.goalHash().equalsIgnoreCase(approval.goalHash())
                || !request.fieldPolicyVersion().equals(approval.fieldPolicyVersion())
                || !request.queryTemplateVersion().equals(approval.queryTemplateVersion())) {
            throw new IllegalStateException("approval binding drifted");
        }
        if (approval.status() != ApprovalStatus.ACTIVE) {
            throw new IllegalStateException("approval is not active");
        }
        if (approval.expiresAt() != null && !approval.expiresAt().isAfter(now)) {
            throw new IllegalStateException("approval has expired");
        }
        if (approval.usedCalls() >= approval.maxCalls()) {
            throw new IllegalStateException("approval call limit is exhausted");
        }
        Instant permitExpiry = now.plus(PERMIT_TTL);
        if (approval.expiresAt() != null && approval.expiresAt().isBefore(permitExpiry)) {
            permitExpiry = approval.expiresAt();
        }
        return new ExecutionPermit(request.taskId(), request.stepId(), request.approvalId(),
                request.scopeRevisionId(), request.sourceBindingId(), approval.approvalDigest(),
                request.idempotencyKey(), now, permitExpiry);
    }

    /** 判断 permit 是否仍在短期有效窗口内。 */
    public boolean isValid(ExecutionPermit permit, Instant at) {
        return permit != null && at != null && !at.isBefore(permit.issuedAt()) && at.isBefore(permit.expiresAt());
    }

    /** 根据步骤目标计算审批卡目标摘要，避免各入口重复手写摘要逻辑。 */
    public static String goalHash(String goal) {
        if (goal == null || goal.isBlank()) {
            throw new IllegalArgumentException("goal is required");
        }
        return Sha256Digest.hex(goal);
    }
}
