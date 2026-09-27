package cn.com.crc.ems.finance.agent.task.application;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.domain.ApprovalStatus;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;
import cn.com.crc.ems.finance.agent.task.domain.StepStatus;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;

/**
 *
 * @Description: 负责审批卡的绑定校验、不可变 revision 追加、复用和撤销。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:40:00
 */
@Service
public class StepApprovalApplicationService {

    private final InvestigationTaskRepository taskRepository;
    private final InvestigationStepRepository stepRepository;
    private final StepApprovalRepository approvalRepository;
    private final TaskAuditEventAppender auditEventAppender;
    private final Clock applicationClock;

    public StepApprovalApplicationService(InvestigationTaskRepository taskRepository,
            InvestigationStepRepository stepRepository, StepApprovalRepository approvalRepository,
            TaskAuditEventAppender auditEventAppender, Clock applicationClock) {
        this.taskRepository = taskRepository;
        this.stepRepository = stepRepository;
        this.approvalRepository = approvalRepository;
        this.auditEventAppender = auditEventAppender;
        this.applicationClock = applicationClock;
    }

    /**
     *
     * @description: 绑定审批边界并追加或复用活动审批 revision。
     *
     * @param: command 审批命令
     * @return: 审批结果，不包含 permit
     * @throws: RepositoryEntityNotFoundException 任务或步骤不存在
     */
    @Transactional
    public StepApprovalResult approve(CreateStepApprovalCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command is required");
        }
        InvestigationTask task = taskRepository.findByIdForUpdate(command.taskId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        if (task.status() != TaskStatus.WAITING_STEP_APPROVAL) {
            throw new IllegalStateException("task is not waiting for step approval");
        }
        if (task.rowVersion() != command.expectedRowVersion()) {
            throw new IllegalStateException("task row version changed");
        }
        InvestigationStep step = stepRepository.findById(command.stepId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation step not found"));
        if (!task.taskId().equals(step.taskId()) || !command.stepId().equals(task.currentStepId())) {
            throw new IllegalStateException("step is not the current task step");
        }
        if (!task.currentScopeRevisionId().equals(command.scopeRevisionId())) {
            throw new IllegalStateException("approval scope is not current");
        }
        String expectedGoalHash = StepExecutionGuard.goalHash(step.goal());
        if (!expectedGoalHash.equalsIgnoreCase(command.goalHash())) {
            throw new IllegalStateException("approval goal digest does not match step");
        }
        if (command.decision() == ApprovalDecision.REJECTED) {
            stepRepository.updateStatus(step.stepId(), StepStatus.CANCELLED, command.actorUserId(), command.occurredAt());
            auditEventAppender.append(UuidGenerator.newUuid(), task.taskId(), command.actorUserId(),
                    "STEP_APPROVAL_REJECTED", "STEP", step.stepId().toString(), command.occurredAt(), payload -> {
                        payload.put("goalHash", command.goalHash());
                        payload.put("scopeRevisionId", command.scopeRevisionId().toString());
                    });
            return new StepApprovalResult(task, null, ApprovalDecision.REJECTED);
        }
        List<StepApproval> history = approvalRepository.findByStepId(step.stepId());
        StepApproval reusable = history.stream().filter(approval -> approval.status() == ApprovalStatus.ACTIVE)
                .filter(approval -> sameBinding(approval, command)).max(Comparator.comparingInt(StepApproval::revisionNo))
                .orElse(null);
        if (reusable != null && reusable.usedCalls() < reusable.maxCalls()) {
            return new StepApprovalResult(task, reusable, ApprovalDecision.APPROVED);
        }
        int revisionNo = history.stream().mapToInt(StepApproval::revisionNo).max().orElse(0) + 1;
        String approvalDigest = approvalDigest(command, revisionNo);
        StepApproval approval = new StepApproval(UuidGenerator.newUuid(), step.stepId(), revisionNo,
                command.actorUserId(), command.occurredAt(), command.expiresAt(), command.goalHash().toLowerCase(),
                command.sourceBindingId(), command.scopeRevisionId(), command.fieldPolicyVersion(),
                command.queryTemplateVersion(), command.maxCalls(), 0, approvalDigest, ApprovalStatus.ACTIVE,
                command.actorUserId(), command.occurredAt(), command.actorUserId(), command.occurredAt());
        approvalRepository.insert(approval);
        stepRepository.updateStatus(step.stepId(), StepStatus.APPROVED, command.actorUserId(), command.occurredAt());
        auditEventAppender.append(UuidGenerator.newUuid(), task.taskId(), command.actorUserId(),
                "STEP_APPROVAL_CREATED", "STEP_APPROVAL", approval.approvalId().toString(), command.occurredAt(),
                payload -> {
                    payload.put("stepId", step.stepId().toString());
                    payload.put("revisionNo", revisionNo);
                    payload.put("scopeRevisionId", command.scopeRevisionId().toString());
                    payload.put("approvalDigest", approvalDigest);
                });
        return new StepApprovalResult(task, approval, ApprovalDecision.APPROVED);
    }

    /** 撤销活动审批并保留其不可变历史记录。 */
    @Transactional
    public StepApproval revoke(RevokeStepApprovalCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command is required");
        }
        InvestigationTask task = taskRepository.findByIdForUpdate(command.taskId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        StepApproval approval = approvalRepository.findById(command.approvalId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("step approval not found"));
        InvestigationStep step = stepRepository.findById(approval.stepId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation step not found"));
        if (!task.taskId().equals(step.taskId())) {
            throw new IllegalStateException("approval does not belong to task");
        }
        if (approval.status() != ApprovalStatus.ACTIVE) {
            return approval;
        }
        approvalRepository.revoke(approval.approvalId(), command.actorUserId(), command.occurredAt());
        auditEventAppender.append(UuidGenerator.newUuid(), task.taskId(), command.actorUserId(),
                "STEP_APPROVAL_REVOKED", "STEP_APPROVAL", approval.approvalId().toString(), command.occurredAt(),
                payload -> payload.put("revisionNo", approval.revisionNo()));
        return approval;
    }

    private static boolean sameBinding(StepApproval approval, CreateStepApprovalCommand command) {
        return approval.scopeRevisionId().equals(command.scopeRevisionId())
                && approval.sourceBindingId().equals(command.sourceBindingId())
                && approval.goalHash().equalsIgnoreCase(command.goalHash())
                && approval.fieldPolicyVersion().equals(command.fieldPolicyVersion())
                && approval.queryTemplateVersion().equals(command.queryTemplateVersion())
                && approval.maxCalls() == command.maxCalls()
                && java.util.Objects.equals(approval.expiresAt(), command.expiresAt());
    }

    private static String approvalDigest(CreateStepApprovalCommand command, int revisionNo) {
        return Sha256Digest.hex(String.join("|", command.taskId().toString(), command.stepId().toString(),
                Integer.toString(revisionNo), command.goalHash().toLowerCase(), command.sourceBindingId().toString(),
                command.scopeRevisionId().toString(), command.fieldPolicyVersion(), command.queryTemplateVersion(),
                Integer.toString(command.maxCalls()), String.valueOf(command.expiresAt())));
    }
}
