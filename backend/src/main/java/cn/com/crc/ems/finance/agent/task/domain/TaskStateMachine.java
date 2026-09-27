package cn.com.crc.ems.finance.agent.task.domain;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @Description: 按附件 A 固化调查任务生命周期、暂停恢复和取消守卫的纯领域状态机。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:40:00
 */
@Component
public final class TaskStateMachine {

    private static final Map<TaskStatus, Set<TaskStatus>> TRANSITIONS = transitions();
    private static final Set<TaskStatus> PAUSED_STATES = EnumSet.of(TaskStatus.PAUSED_MODEL_UNAVAILABLE,
            TaskStatus.PAUSED_BUDGET, TaskStatus.PAUSED_SOURCE_UNAVAILABLE, TaskStatus.FAILED_RECOVERABLE);
    private static final Set<TaskStatus> RESUME_TARGETS = EnumSet.of(TaskStatus.WAITING_STEP_APPROVAL,
            TaskStatus.RUNNING_STEP, TaskStatus.ANALYZING);

    /**
     *
     * @description: 校验普通、暂停恢复或取消状态转移。
     *
     * @param: current 当前任务状态
     * @param: target 目标任务状态
     * @param: reason 转移原因
     * @param: approvalStillValid 恢复到运行/分析状态时审批是否仍然有效
     * @return: 已通过校验的状态转移
     * @throws: InvalidTaskTransitionException 当前状态、目标或守卫不满足
     */
    public TaskTransition transition(TaskStatus current, TaskStatus target, String reason,
            boolean approvalStillValid) {
        requireStatuses(current, target, reason);
        if (current == TaskStatus.CLOSED || current == TaskStatus.CANCELLED) {
            throw new InvalidTaskTransitionException("terminal task cannot transition");
        }
        if (target == TaskStatus.CANCELLED) {
            return new TaskTransition(current, target, reason);
        }
        if (PAUSED_STATES.contains(current)) {
            if (!RESUME_TARGETS.contains(target)) {
                throw new InvalidTaskTransitionException("paused task has no such resume target");
            }
            if (target != TaskStatus.WAITING_STEP_APPROVAL && !approvalStillValid) {
                throw new InvalidTaskTransitionException("resume requires a valid approval");
            }
            return new TaskTransition(current, target, reason);
        }
        if (!TRANSITIONS.getOrDefault(current, Set.of()).contains(target)) {
            throw new InvalidTaskTransitionException("task transition is not allowed");
        }
        return new TaskTransition(current, target, reason);
    }

    /** 返回固定转移表的只读快照，供契约测试和运维文档使用。 */
    public Map<TaskStatus, Set<TaskStatus>> transitionTable() {
        return TRANSITIONS;
    }

    private static void requireStatuses(TaskStatus current, TaskStatus target, String reason) {
        if (current == null || target == null) {
            throw new InvalidTaskTransitionException("current and target status are required");
        }
        if (reason == null || reason.isBlank()) {
            throw new InvalidTaskTransitionException("transition reason is required");
        }
        if (current == target) {
            throw new InvalidTaskTransitionException("self transition is not allowed");
        }
    }

    private static Map<TaskStatus, Set<TaskStatus>> transitions() {
        EnumMap<TaskStatus, Set<TaskStatus>> result = new EnumMap<>(TaskStatus.class);
        allow(result, TaskStatus.DRAFT, TaskStatus.WAITING_SCOPE_CONFIRMATION);
        allow(result, TaskStatus.WAITING_SCOPE_CONFIRMATION, TaskStatus.READY);
        allow(result, TaskStatus.READY, TaskStatus.WAITING_SCOPE_CONFIRMATION, TaskStatus.WAITING_STEP_APPROVAL);
        allow(result, TaskStatus.WAITING_STEP_APPROVAL, TaskStatus.RUNNING_STEP, TaskStatus.STALE_APPROVAL);
        allow(result, TaskStatus.RUNNING_STEP, TaskStatus.ANALYZING, TaskStatus.FAILED_RECOVERABLE,
                TaskStatus.PAUSED_MODEL_UNAVAILABLE, TaskStatus.PAUSED_BUDGET, TaskStatus.PAUSED_SOURCE_UNAVAILABLE);
        allow(result, TaskStatus.ANALYZING, TaskStatus.WAITING_STEP_APPROVAL,
                TaskStatus.WAITING_PROFESSIONAL_CONFIRMATION, TaskStatus.DRAFT_PACKET,
                TaskStatus.FAILED_RECOVERABLE, TaskStatus.PAUSED_MODEL_UNAVAILABLE, TaskStatus.PAUSED_BUDGET,
                TaskStatus.PAUSED_SOURCE_UNAVAILABLE);
        allow(result, TaskStatus.WAITING_PROFESSIONAL_CONFIRMATION, TaskStatus.DRAFT_PACKET);
        allow(result, TaskStatus.DRAFT_PACKET, TaskStatus.WAITING_REVIEW);
        allow(result, TaskStatus.WAITING_REVIEW, TaskStatus.RECHECKING_SCOPE);
        allow(result, TaskStatus.RECHECKING_SCOPE, TaskStatus.DRAFT_PACKET, TaskStatus.READY_TO_PUBLISH);
        allow(result, TaskStatus.READY_TO_PUBLISH, TaskStatus.PUBLISHED);
        allow(result, TaskStatus.PUBLISHED, TaskStatus.HANDED_OFF);
        allow(result, TaskStatus.HANDED_OFF, TaskStatus.CLOSED);
        allow(result, TaskStatus.FAILED_RECOVERABLE, TaskStatus.WAITING_STEP_APPROVAL, TaskStatus.RUNNING_STEP);
        allow(result, TaskStatus.STALE_APPROVAL, TaskStatus.WAITING_STEP_APPROVAL);
        result.replaceAll((status, targets) -> Set.copyOf(targets));
        return Map.copyOf(result);
    }

    private static void allow(Map<TaskStatus, Set<TaskStatus>> table, TaskStatus from, TaskStatus... targets) {
        table.computeIfAbsent(from, ignored -> EnumSet.noneOf(TaskStatus.class)).addAll(Set.of(targets));
    }
}
