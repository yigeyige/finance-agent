package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/** 使用参数化 SQL 访问调查任务并执行乐观版本更新。 */
@Repository
public class JdbcInvestigationTaskRepository implements InvestigationTaskRepository {

    private static final String SELECT = "SELECT task_id, title, original_question, initiator_user_id, status, "
            + "runtime_bundle_id, current_scope_revision_id, current_step_id, created_at, updated_at, row_version, "
            + "closed_at, created_by, updated_by FROM task.investigation_task ";

    private final JdbcTemplate jdbcTemplate;

    public JdbcInvestigationTaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<InvestigationTask> findById(UUID taskId) {
        return jdbcTemplate.query(SELECT + "WHERE task_id = ?", (rs, rowNum) -> JdbcTaskMappings.task(rs), taskId)
                .stream().findFirst();
    }

    @Override
    public void insert(InvestigationTask task) {
        jdbcTemplate.update("INSERT INTO task.investigation_task (task_id, title, original_question, initiator_user_id, "
                        + "status, runtime_bundle_id, current_scope_revision_id, current_step_id, created_at, updated_at, "
                        + "row_version, closed_at, created_by, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                task.taskId(), task.title(), task.originalQuestion(), task.initiatorUserId(), task.status().name(),
                task.runtimeBundleId(), task.currentScopeRevisionId(), task.currentStepId(), Timestamp.from(task.createdAt()),
                Timestamp.from(task.updatedAt()), task.rowVersion(), timestamp(task.closedAt()), task.createdBy(), task.updatedBy());
    }

    @Override
    public boolean updateWithVersion(InvestigationTask task, long expectedRowVersion) {
        int updated = jdbcTemplate.update("UPDATE task.investigation_task SET title = ?, original_question = ?, status = ?, "
                        + "runtime_bundle_id = ?, current_scope_revision_id = ?, current_step_id = ?, updated_at = ?, "
                        + "row_version = ?, closed_at = ?, updated_by = ? WHERE task_id = ? AND row_version = ?",
                task.title(), task.originalQuestion(), task.status().name(), task.runtimeBundleId(),
                task.currentScopeRevisionId(), task.currentStepId(), Timestamp.from(task.updatedAt()), task.rowVersion(),
                timestamp(task.closedAt()), task.updatedBy(), task.taskId(), expectedRowVersion);
        return updated == 1;
    }

    private static Timestamp timestamp(java.time.Instant value) {
        return value == null ? null : Timestamp.from(value);
    }
}
