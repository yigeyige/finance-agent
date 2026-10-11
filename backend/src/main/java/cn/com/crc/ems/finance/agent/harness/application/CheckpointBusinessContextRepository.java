package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @Description: 为 harness 提供已经组合好的任务业务上下文，隔离任务域仓储依赖。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface CheckpointBusinessContextRepository {

    /** 按 checkpoint 绑定查询当前任务、步骤、范围和审批上下文。 */
    Optional<CheckpointBusinessContext> find(UUID taskId, UUID stepId, UUID scopeRevisionId, UUID approvalId);

    /** checkpoint 恢复所需的只读业务绑定快照。 */
    record CheckpointBusinessContext(UUID taskId, UUID currentStepId, UUID currentScopeRevisionId,
            UUID runtimeBundleId, boolean stepBindingAvailable, boolean scopeBindingAvailable,
            boolean approvalBindingAvailable, boolean approvalActive, Instant approvalExpiresAt,
            int approvalMaxCalls, int approvalUsedCalls) {

        public CheckpointBusinessContext {
            if (taskId == null || currentStepId == null || currentScopeRevisionId == null || runtimeBundleId == null) {
                throw new IllegalArgumentException("task business binding is required");
            }
            if (approvalMaxCalls < 0 || approvalUsedCalls < 0) {
                throw new IllegalArgumentException("approval call limits must not be negative");
            }
        }
    }
}
