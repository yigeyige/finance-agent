package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskCancelCommand;
import cn.com.crc.ems.finance.agent.task.application.TaskControlErrorCode;
import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskPauseCommand;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionCommand;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionResult;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 *
 * @Description: 统一处理暂停和取消，确保取消释放租约但不删除任务历史。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Service
public class TaskControlService {

    private final TaskStateTransitionService stateTransitionService;
    private final TaskLeaseRepository leaseRepository;
    private final InvestigationTaskRepository taskRepository;

    public TaskControlService(TaskStateTransitionService stateTransitionService, TaskLeaseRepository leaseRepository,
            InvestigationTaskRepository taskRepository) {
        this.stateTransitionService = stateTransitionService;
        this.leaseRepository = leaseRepository;
        this.taskRepository = taskRepository;
    }

    /** 持久化暂停状态事件；暂停任务不会再被 Worker 的领取查询选中。 */
    @Transactional
    public TaskStateTransitionResult pause(TaskPauseCommand command) {
        return stateTransitionService.transition(new TaskStateTransitionCommand(command.taskId(), pauseStatus(command.errorCode()),
                command.reason(), command.actorUserId(), command.occurredAt(), true));
    }

    /** 先推进取消状态，再按 owner 释放活动租约，保留所有历史记录。 */
    @Transactional
    public TaskStateTransitionResult cancel(TaskCancelCommand command) {
        TaskStateTransitionResult result = stateTransitionService.transition(new TaskStateTransitionCommand(
                command.taskId(), TaskStatus.CANCELLED, command.reason(), command.actorUserId(), command.occurredAt(), true));
        leaseRepository.findActiveByTaskId(command.taskId()).ifPresent(lease -> {
            if (!leaseRepository.release(lease.leaseId(), lease.ownerInstance(), command.occurredAt(), command.actorUserId())) {
                throw new IllegalStateException("active task lease was not released");
            }
        });
        return result;
    }

    private static TaskStatus pauseStatus(TaskControlErrorCode code) {
        return switch (code) {
            case MODEL_UNAVAILABLE -> TaskStatus.PAUSED_MODEL_UNAVAILABLE;
            case SOURCE_UNAVAILABLE -> TaskStatus.PAUSED_SOURCE_UNAVAILABLE;
            case BUDGET_EXHAUSTED -> TaskStatus.PAUSED_BUDGET;
            case RECOVERABLE_FAILURE -> TaskStatus.FAILED_RECOVERABLE;
            case TASK_CANCELLED -> throw new IllegalArgumentException("cancel error code cannot pause");
        };
    }
}
