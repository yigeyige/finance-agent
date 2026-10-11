package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.TaskLeaseEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.TaskLeaseMapper;

/**
 *
 * @Description: 使用 MyBatis-Plus Mapper 管理数据库部分唯一索引保护的活动租约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisTaskLeaseRepository implements TaskLeaseRepository {

    private final TaskLeaseMapper mapper;

    public MybatisTaskLeaseRepository(TaskLeaseMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<TaskLease> findActiveByTaskId(UUID taskId) {
        return mapper.selectList(new QueryWrapper<TaskLeaseEntity>().eq("task_id", taskId)
                        .isNull("released_at").last("LIMIT 1")).stream().findFirst().map(MybatisTaskMappings::toDomain);
    }

    @Override
    public List<TaskLease> findExpired(Instant observedAt, int limit) {
        if (observedAt == null || limit < 1 || limit > 1000) {
            throw new IllegalArgumentException("observedAt and bounded positive limit are required");
        }
        return mapper.selectExpired(observedAt, limit).stream().map(MybatisTaskMappings::toDomain).toList();
    }

    @Override
    public void acquire(TaskLease lease) {
        mapper.insert(MybatisTaskMappings.toEntity(lease));
    }

    @Override
    public boolean heartbeat(UUID leaseId, Instant heartbeatAt, Instant expiresAt, UUID actorId) {
        return heartbeat(leaseId, null, heartbeatAt, expiresAt, actorId);
    }

    @Override
    public boolean heartbeat(UUID leaseId, String ownerInstance, Instant heartbeatAt, Instant expiresAt, UUID actorId) {
        UpdateWrapper<TaskLeaseEntity> update = activeLease(leaseId, ownerInstance)
                .set("heartbeat_at", heartbeatAt).set("expires_at", expiresAt).set("updated_at", heartbeatAt)
                .set("updated_by", actorId);
        return mapper.update(null, update) == 1;
    }

    @Override
    public boolean release(UUID leaseId, Instant releasedAt, UUID actorId) {
        return release(leaseId, null, releasedAt, actorId);
    }

    @Override
    public boolean release(UUID leaseId, String ownerInstance, Instant releasedAt, UUID actorId) {
        UpdateWrapper<TaskLeaseEntity> update = activeLease(leaseId, ownerInstance)
                .set("released_at", releasedAt).set("updated_at", releasedAt).set("updated_by", actorId);
        return mapper.update(null, update) == 1;
    }

    private UpdateWrapper<TaskLeaseEntity> activeLease(UUID leaseId, String ownerInstance) {
        UpdateWrapper<TaskLeaseEntity> update = new UpdateWrapper<TaskLeaseEntity>().eq("lease_id", leaseId)
                .isNull("released_at");
        if (ownerInstance != null) {
            update.eq("owner_instance", ownerInstance);
        }
        return update;
    }
}
