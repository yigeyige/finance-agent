package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;

/** 使用数据库部分唯一索引管理单任务活动租约。 */
@Repository
public class JdbcTaskLeaseRepository implements TaskLeaseRepository {

    private static final String SELECT = "SELECT lease_id, task_id, step_id, owner_instance, acquired_at, heartbeat_at, "
            + "expires_at, released_at, created_by, created_at, updated_by, updated_at FROM task.task_lease ";

    private final JdbcTemplate jdbcTemplate;

    public JdbcTaskLeaseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<TaskLease> findActiveByTaskId(UUID taskId) {
        return jdbcTemplate.query(SELECT + "WHERE task_id = ? AND released_at IS NULL", (rs, rowNum) -> JdbcTaskMappings.lease(rs), taskId)
                .stream().findFirst();
    }

    @Override
    public void acquire(TaskLease lease) {
        jdbcTemplate.update("INSERT INTO task.task_lease (lease_id, task_id, step_id, owner_instance, acquired_at, heartbeat_at, "
                        + "expires_at, released_at, created_by, created_at, updated_by, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                lease.leaseId(), lease.taskId(), lease.stepId(), lease.ownerInstance(), Timestamp.from(lease.acquiredAt()),
                Timestamp.from(lease.heartbeatAt()), Timestamp.from(lease.expiresAt()), timestamp(lease.releasedAt()), lease.createdBy(),
                Timestamp.from(lease.createdAt()), lease.updatedBy(), Timestamp.from(lease.updatedAt()));
    }

    @Override
    public boolean heartbeat(UUID leaseId, java.time.Instant heartbeatAt, java.time.Instant expiresAt, UUID actorId) {
        return jdbcTemplate.update("UPDATE task.task_lease SET heartbeat_at = ?, expires_at = ?, updated_at = ?, updated_by = ? "
                        + "WHERE lease_id = ? AND released_at IS NULL", Timestamp.from(heartbeatAt), Timestamp.from(expiresAt),
                Timestamp.from(heartbeatAt), actorId, leaseId) == 1;
    }

    @Override
    public boolean release(UUID leaseId, java.time.Instant releasedAt, UUID actorId) {
        return jdbcTemplate.update("UPDATE task.task_lease SET released_at = ?, updated_at = ?, updated_by = ? "
                        + "WHERE lease_id = ? AND released_at IS NULL", Timestamp.from(releasedAt), Timestamp.from(releasedAt), actorId, leaseId) == 1;
    }

    private static Timestamp timestamp(java.time.Instant value) {
        return value == null ? null : Timestamp.from(value);
    }
}
