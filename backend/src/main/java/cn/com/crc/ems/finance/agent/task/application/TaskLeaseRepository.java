package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.TaskLease;

/** 任务执行租约持久化端口。 */
public interface TaskLeaseRepository {

    /** 查询任务当前未释放租约。 */
    Optional<TaskLease> findActiveByTaskId(UUID taskId);

    /** 尝试获取租约，数据库唯一索引负责并发冲突。 */
    void acquire(TaskLease lease);

    /** 更新心跳。 */
    boolean heartbeat(UUID leaseId, Instant heartbeatAt, Instant expiresAt, UUID actorId);

    /** 释放租约。 */
    boolean release(UUID leaseId, Instant releasedAt, UUID actorId);
}
