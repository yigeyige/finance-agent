package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.TaskLeaseEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.TaskLeaseMapper;

/** 使用 MyBatis-Plus Mapper 管理数据库部分唯一索引保护的活动租约。 */
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
    public void acquire(TaskLease lease) {
        mapper.insert(MybatisTaskMappings.toEntity(lease));
    }

    @Override
    public boolean heartbeat(UUID leaseId, Instant heartbeatAt, Instant expiresAt, UUID actorId) {
        return mapper.update(null, new UpdateWrapper<TaskLeaseEntity>().eq("lease_id", leaseId).isNull("released_at")
                .set("heartbeat_at", heartbeatAt).set("expires_at", expiresAt).set("updated_at", heartbeatAt)
                .set("updated_by", actorId)) == 1;
    }

    @Override
    public boolean release(UUID leaseId, Instant releasedAt, UUID actorId) {
        return mapper.update(null, new UpdateWrapper<TaskLeaseEntity>().eq("lease_id", leaseId).isNull("released_at")
                .set("released_at", releasedAt).set("updated_at", releasedAt).set("updated_by", actorId)) == 1;
    }
}
