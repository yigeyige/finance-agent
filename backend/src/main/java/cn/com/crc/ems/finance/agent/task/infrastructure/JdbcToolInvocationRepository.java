package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.task.application.ToolInvocationRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;

/** 使用幂等键唯一约束保存可恢复工具调用。 */
@Repository
public class JdbcToolInvocationRepository implements ToolInvocationRepository {

    private static final String SELECT = "SELECT invocation_id, task_id, step_id, approval_id, tool_code, idempotency_key, "
            + "request_digest, response_digest, status, started_at, completed_at, error_code, created_by, created_at, updated_by, updated_at "
            + "FROM task.tool_invocation ";

    private final JdbcTemplate jdbcTemplate;

    public JdbcToolInvocationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<ToolInvocation> findByIdempotencyKey(String idempotencyKey) {
        return jdbcTemplate.query(SELECT + "WHERE idempotency_key = ?", (rs, rowNum) -> JdbcTaskMappings.invocation(rs), idempotencyKey)
                .stream().findFirst();
    }

    @Override
    public void insert(ToolInvocation invocation) {
        jdbcTemplate.update("INSERT INTO task.tool_invocation (invocation_id, task_id, step_id, approval_id, tool_code, idempotency_key, "
                        + "request_digest, response_digest, status, started_at, completed_at, error_code, created_by, created_at, updated_by, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", invocation.invocationId(), invocation.taskId(),
                invocation.stepId(), invocation.approvalId(), invocation.toolCode(), invocation.idempotencyKey(), invocation.requestDigest(),
                invocation.responseDigest(), invocation.status().name(), Timestamp.from(invocation.startedAt()), timestamp(invocation.completedAt()),
                invocation.errorCode(), invocation.createdBy(), Timestamp.from(invocation.createdAt()), invocation.updatedBy(), Timestamp.from(invocation.updatedAt()));
    }

    @Override
    public boolean updateStatus(String idempotencyKey, InvocationStatus status, String responseDigest, String errorCode,
            java.time.Instant completedAt, java.time.Instant updatedAt, java.util.UUID actorId) {
        return jdbcTemplate.update("UPDATE task.tool_invocation SET status = ?, response_digest = ?, error_code = ?, completed_at = ?, "
                        + "updated_at = ?, updated_by = ? WHERE idempotency_key = ?", status.name(), responseDigest, errorCode,
                timestamp(completedAt), Timestamp.from(updatedAt), actorId, idempotencyKey) == 1;
    }

    private static Timestamp timestamp(java.time.Instant value) {
        return value == null ? null : Timestamp.from(value);
    }
}
