package cn.com.crc.ems.finance.agent.task.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.StepStatus;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;

/**
 *
 * @Description: 负责保存有界步骤提议并把 READY 任务推进到等待审批状态。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:40:00
 */
@Service
public class StepProposalApplicationService {

    private final InvestigationTaskRepository taskRepository;
    private final InvestigationStepRepository stepRepository;
    private final TaskStateTransitionService stateTransitionService;
    private final TaskAuditEventAppender auditEventAppender;
    private final Clock applicationClock;

    public StepProposalApplicationService(InvestigationTaskRepository taskRepository,
            InvestigationStepRepository stepRepository, TaskStateTransitionService stateTransitionService,
            TaskAuditEventAppender auditEventAppender, Clock applicationClock) {
        this.taskRepository = taskRepository;
        this.stepRepository = stepRepository;
        this.stateTransitionService = stateTransitionService;
        this.auditEventAppender = auditEventAppender;
        this.applicationClock = applicationClock;
    }

    /**
     *
     * @description: 校验推荐/备选数量、当前范围和任务状态后追加步骤提议。
     *
     * @param: command 步骤提议命令
     * @return: 追加后的任务和步骤
     * @throws: RepositoryEntityNotFoundException 任务不存在
     */
    @Transactional
    public StepProposalResult propose(ProposeStepsCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command is required");
        }
        validateCardinality(command.steps());
        InvestigationTask current = taskRepository.findByIdForUpdate(command.taskId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        if (current.status() != TaskStatus.READY || current.currentScopeRevisionId() == null) {
            throw new IllegalStateException("task must be READY with a confirmed scope");
        }
        List<InvestigationStep> existing = stepRepository.findByTaskId(current.taskId());
        if (existing.stream().anyMatch(step -> step.status() == StepStatus.PROPOSED
                || step.status() == StepStatus.WAITING_APPROVAL || step.status() == StepStatus.APPROVED
                || step.status() == StepStatus.RUNNING)) {
            throw new IllegalStateException("task already has an active step proposal");
        }
        Instant occurredAt = command.occurredAt();
        int nextSequence = existing.stream().mapToInt(InvestigationStep::sequenceNo).max().orElse(0) + 1;
        List<InvestigationStep> steps = new ArrayList<>();
        for (StepProposalItem item : command.steps()) {
            InvestigationStep step = new InvestigationStep(UuidGenerator.newUuid(), current.taskId(), nextSequence++,
                    item.goal(), item.recommended(), StepStatus.PROPOSED, occurredAt, null,
                    command.actorUserId(), occurredAt, command.actorUserId());
            stepRepository.insert(step);
            steps.add(step);
        }
        UUID recommendedStepId = steps.stream().filter(InvestigationStep::recommended)
                .findFirst().orElseThrow().stepId();
        InvestigationTask transitioned = stateTransitionService.transition(new TaskStateTransitionCommand(
                current.taskId(), TaskStatus.WAITING_STEP_APPROVAL, "step proposal created", command.actorUserId(),
                occurredAt, false, current.currentScopeRevisionId(), recommendedStepId)).task();
        auditEventAppender.append(UuidGenerator.newUuid(), current.taskId(), command.actorUserId(),
                "STEP_PROPOSAL_CREATED", "TASK", current.taskId().toString(), occurredAt, payload -> {
                    payload.put("stepCount", steps.size());
                    payload.put("recommendedStepId", recommendedStepId.toString());
                    payload.put("scopeRevisionId", current.currentScopeRevisionId().toString());
                });
        return new StepProposalResult(transitioned, steps);
    }

    private static void validateCardinality(List<StepProposalItem> steps) {
        if (steps == null || steps.isEmpty() || steps.size() > 3) {
            throw new IllegalArgumentException("steps must contain one recommendation and at most two alternatives");
        }
        long recommendations = steps.stream().filter(StepProposalItem::recommended).count();
        if (recommendations != 1) {
            throw new IllegalArgumentException("steps must contain exactly one recommendation");
        }
    }
}
