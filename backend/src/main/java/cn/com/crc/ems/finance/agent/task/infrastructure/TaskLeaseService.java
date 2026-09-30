package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PostgresRepositoryExceptionMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.application.InvestigationStepRepository;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskLeaseClaim;
import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionCommand;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.StepStatus;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 *
 * @Description: 统一编排步骤领取、租约生命周期和过期接管的短事务应用服务。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-28 10:00:00
 */
@Service
public class TaskLeaseService {

    private final InvestigationTaskRepository taskRepository;
    private final InvestigationStepRepository stepRepository;
    private final TaskLeaseRepository leaseRepository;
    private final TaskStateTransitionService stateTransitionService;

    public TaskLeaseService(InvestigationTaskRepository taskRepository, InvestigationStepRepository stepRepository,
            TaskLeaseRepository leaseRepository, TaskStateTransitionService stateTransitionService) {
        this.taskRepository = taskRepository;
        this.stepRepository = stepRepository;
        this.leaseRepository = leaseRepository;
        this.stateTransitionService = stateTransitionService;
    }

    /**
     *
     * @description: 在一个短事务中领取已获批步骤并推进任务/步骤状态。
     *
     * @param: ownerInstance Worker 实例标识
     * @param: actorUserId 执行审计主体，可为空
     * @param: observedAt 本次领取使用的观察时刻
     * @param: leaseDuration 租约有效时长
     * @return: 已领取步骤和活动租约；没有可执行步骤时为空
     * @throws: RepositoryEntityNotFoundException 步骤所属任务不存在
     */
    @Transactional
    public Optional<TaskLeaseClaim> claimNext(String ownerInstance, UUID actorUserId, Instant observedAt,
            Duration leaseDuration) {
        validateLeaseInput(ownerInstance, observedAt, leaseDuration);
        Optional<InvestigationStep> candidate = stepRepository.findNextForUpdateSkipLocked(
                TaskStatus.WAITING_STEP_APPROVAL.name(), StepStatus.APPROVED.name());
        if (candidate.isEmpty()) {
            return Optional.empty();
        }
        InvestigationStep step = candidate.get();
        taskRepository.findByIdForUpdate(step.taskId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        TaskLease lease = newLease(step, ownerInstance, actorUserId, observedAt, leaseDuration);
        acquire(lease);
        if (stepRepository.updateStatus(step.stepId(), StepStatus.RUNNING, actorUserId, observedAt) != 1) {
            throw new IllegalStateException("step status update did not affect one row");
        }
        stateTransitionService.transition(new TaskStateTransitionCommand(step.taskId(), TaskStatus.RUNNING_STEP,
                "worker claimed approved step", actorUserId, observedAt, true, null, step.stepId()));
        return Optional.of(new TaskLeaseClaim(step, lease));
    }

    /**
     *
     * @description: 在任务行锁内领取指定任务当前已获批步骤，避免本地受控执行器误领其他任务。
     *
     * @param: taskId 指定任务标识
     * @param: ownerInstance Worker 实例标识
     * @param: actorUserId 执行审计主体
     * @param: observedAt 本次领取使用的观察时刻
     * @param: leaseDuration 租约有效时长
     * @return: 已领取步骤和活动租约
     * @throws: RepositoryEntityNotFoundException 任务或当前步骤不存在
     * @author: yigeyige99
     */
    @Transactional
    public TaskLeaseClaim claim(UUID taskId, String ownerInstance, UUID actorUserId, Instant observedAt,
            Duration leaseDuration) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        validateLeaseInput(ownerInstance, observedAt, leaseDuration);
        var task = taskRepository.findByIdForUpdate(taskId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        if (task.status() != TaskStatus.WAITING_STEP_APPROVAL || task.currentStepId() == null) {
            throw new IllegalStateException("task has no approved current step to claim");
        }
        InvestigationStep step = stepRepository.findById(task.currentStepId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation step not found"));
        if (!taskId.equals(step.taskId()) || step.status() != StepStatus.APPROVED) {
            throw new IllegalStateException("current task step is not approved");
        }
        TaskLease lease = newLease(step, ownerInstance, actorUserId, observedAt, leaseDuration);
        acquire(lease);
        if (stepRepository.updateStatus(step.stepId(), StepStatus.RUNNING, actorUserId, observedAt) != 1) {
            throw new IllegalStateException("step status update did not affect one row");
        }
        stateTransitionService.transition(new TaskStateTransitionCommand(taskId, TaskStatus.RUNNING_STEP,
                "worker claimed approved step", actorUserId, observedAt, true, null, step.stepId()));
        return new TaskLeaseClaim(step, lease);
    }

    /** 严格按过期边界接管任务的旧租约，未过期或边界相等时返回空。 */
    @Transactional
    public Optional<TaskLease> takeOverExpired(UUID taskId, String ownerInstance, UUID actorUserId,
            Instant observedAt, Duration leaseDuration) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        validateLeaseInput(ownerInstance, observedAt, leaseDuration);
        taskRepository.findByIdForUpdate(taskId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        Optional<TaskLease> current = leaseRepository.findActiveByTaskId(taskId);
        if (current.isEmpty() || !current.get().isExpiredAt(observedAt)) {
            return Optional.empty();
        }
        TaskLease expired = current.get();
        if (!leaseRepository.release(expired.leaseId(), expired.ownerInstance(), observedAt, actorUserId)) {
            return Optional.empty();
        }
        TaskLease replacement = new TaskLease(UuidGenerator.newUuid(), expired.taskId(), expired.stepId(), ownerInstance,
                observedAt, observedAt, observedAt.plus(leaseDuration), null, actorUserId, observedAt, actorUserId,
                observedAt);
        acquire(replacement);
        return Optional.of(replacement);
    }

    /** 扫描有限批次过期租约并逐个按任务锁接管，供 Worker 重启恢复使用。 */
    @Transactional
    public List<TaskLease> recoverExpired(String ownerInstance, UUID actorUserId, Instant observedAt,
            Duration leaseDuration, int limit) {
        validateLeaseInput(ownerInstance, observedAt, leaseDuration);
        if (limit < 1 || limit > 1000) {
            throw new IllegalArgumentException("limit must be between 1 and 1000");
        }
        return leaseRepository.findExpired(observedAt, limit).stream()
                .map(expired -> takeOverExpired(expired.taskId(), ownerInstance, actorUserId, observedAt,
                        leaseDuration))
                .flatMap(Optional::stream)
                .toList();
    }

    /** 按 owner 更新心跳，返回 false 表示租约已经丢失或被接管。 */
    @Transactional
    public boolean heartbeat(TaskLease lease, Instant heartbeatAt, Instant expiresAt, UUID actorUserId) {
        if (lease == null || heartbeatAt == null || expiresAt == null || !expiresAt.isAfter(heartbeatAt)) {
            throw new IllegalArgumentException("lease and valid heartbeat timestamps are required");
        }
        return leaseRepository.heartbeat(lease.leaseId(), lease.ownerInstance(), heartbeatAt, expiresAt, actorUserId);
    }

    /** 按 owner 释放租约，返回 false 表示租约已经丢失或被接管。 */
    @Transactional
    public boolean release(TaskLease lease, Instant releasedAt, UUID actorUserId) {
        if (lease == null || releasedAt == null) {
            throw new IllegalArgumentException("lease and releasedAt are required");
        }
        return leaseRepository.release(lease.leaseId(), lease.ownerInstance(), releasedAt, actorUserId);
    }

    private void acquire(TaskLease lease) {
        try {
            leaseRepository.acquire(lease);
        } catch (RuntimeException failure) {
            throw PostgresRepositoryExceptionMapper.mapLeaseAcquisition("lease acquisition", failure);
        }
    }

    private static TaskLease newLease(InvestigationStep step, String ownerInstance, UUID actorUserId,
            Instant observedAt, Duration leaseDuration) {
        return new TaskLease(UuidGenerator.newUuid(), step.taskId(), step.stepId(), ownerInstance, observedAt,
                observedAt, observedAt.plus(leaseDuration), null, actorUserId, observedAt, actorUserId, observedAt);
    }

    private static void validateLeaseInput(String ownerInstance, Instant observedAt, Duration leaseDuration) {
        if (ownerInstance == null || ownerInstance.isBlank() || observedAt == null || leaseDuration == null
                || leaseDuration.isZero() || leaseDuration.isNegative()) {
            throw new IllegalArgumentException("ownerInstance, observedAt and positive leaseDuration are required");
        }
    }
}
