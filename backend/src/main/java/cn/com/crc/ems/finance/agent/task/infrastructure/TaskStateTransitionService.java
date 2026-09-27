package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskStateEventRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionCommand;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionResult;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.InvalidTaskTransitionException;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateEvent;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateMachine;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 *
 * @Description: 任务状态的唯一应用推进入口，负责行锁、状态守卫、乐观版本和状态事件事务。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:45:00
 */
@Service
public class TaskStateTransitionService {

    private final InvestigationTaskRepository taskRepository;
    private final TaskStateEventRepository stateEventRepository;
    private final TaskStateMachine stateMachine;

    public TaskStateTransitionService(InvestigationTaskRepository taskRepository,
            TaskStateEventRepository stateEventRepository, TaskStateMachine stateMachine) {
        this.taskRepository = taskRepository;
        this.stateEventRepository = stateEventRepository;
        this.stateMachine = stateMachine;
    }

    /**
     *
     * @description: 锁定任务并以 row_version 乐观条件更新状态，再追加状态事件。
     *
     * @param: command 状态推进命令
     * @return: 新任务快照和已追加状态事件
     * @throws: RepositoryEntityNotFoundException 任务不存在
     */
    @Transactional
    public TaskStateTransitionResult transition(TaskStateTransitionCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command is required");
        }
        InvestigationTask current = taskRepository.findByIdForUpdate(command.taskId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        stateMachine.transition(current.status(), command.targetStatus(), command.reason(),
                command.approvalStillValid());
        if (requiresConfirmedScope(command.targetStatus()) && current.currentScopeRevisionId() == null
                && command.scopeRevisionId() == null) {
            throw new InvalidTaskTransitionException("confirmed scope is required before step execution");
        }
        InvestigationTask updated = updatedTask(current, command.targetStatus(), command.scopeRevisionId(),
                command.occurredAt());
        taskRepository.updateWithVersionOrThrow(updated, current.rowVersion());
        TaskStateEvent event = new TaskStateEvent(UuidGenerator.newUuid(), current.taskId(), current.status(),
                command.targetStatus(), command.reason(), command.actorUserId(), command.occurredAt(),
                current.rowVersion() + 1);
        stateEventRepository.append(event);
        return new TaskStateTransitionResult(updated, event);
    }

    private static boolean requiresConfirmedScope(TaskStatus target) {
        return target == TaskStatus.WAITING_STEP_APPROVAL || target == TaskStatus.RUNNING_STEP
                || target == TaskStatus.ANALYZING || target == TaskStatus.WAITING_PROFESSIONAL_CONFIRMATION
                || target == TaskStatus.DRAFT_PACKET || target == TaskStatus.WAITING_REVIEW
                || target == TaskStatus.RECHECKING_SCOPE || target == TaskStatus.READY_TO_PUBLISH
                || target == TaskStatus.PUBLISHED || target == TaskStatus.HANDED_OFF || target == TaskStatus.CLOSED;
    }

    private static InvestigationTask updatedTask(InvestigationTask current, TaskStatus target,
            java.util.UUID scopeRevisionId, Instant occurredAt) {
        Instant closedAt = target == TaskStatus.CLOSED || target == TaskStatus.CANCELLED
                        ? occurredAt : current.closedAt();
        return new InvestigationTask(current.taskId(), current.title(), current.originalQuestion(),
                current.initiatorUserId(), target, current.runtimeBundleId(),
                scopeRevisionId == null ? current.currentScopeRevisionId() : scopeRevisionId,
                current.currentStepId(), current.createdAt(), occurredAt, current.rowVersion() + 1, closedAt,
                current.createdBy(), current.updatedBy());
    }
}
