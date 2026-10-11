package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.TaskLease;

/**
 *
 * @Description: 任务执行租约持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface TaskLeaseRepository {

    /** 查询任务当前未释放租约。 */
    Optional<TaskLease> findActiveByTaskId(UUID taskId);

    /** 查询严格早于观察时刻的未释放租约，供 Worker 启动恢复使用。 */
    default List<TaskLease> findExpired(Instant observedAt, int limit) {
        return List.of();
    }

    /** 尝试获取租约，数据库唯一索引负责并发冲突。 */
    void acquire(TaskLease lease);

    /** 更新心跳。 */
    boolean heartbeat(UUID leaseId, Instant heartbeatAt, Instant expiresAt, UUID actorId);

    /** 仅允许指定 Worker 持有者更新未释放租约的心跳。 */
    default boolean heartbeat(UUID leaseId, String ownerInstance, Instant heartbeatAt, Instant expiresAt,
            UUID actorId) {
        return heartbeat(leaseId, heartbeatAt, expiresAt, actorId);
    }

    /** 释放租约。 */
    boolean release(UUID leaseId, Instant releasedAt, UUID actorId);

    /** 仅允许指定 Worker 持有者释放未释放租约。 */
    default boolean release(UUID leaseId, String ownerInstance, Instant releasedAt, UUID actorId) {
        return release(leaseId, releasedAt, actorId);
    }
}
