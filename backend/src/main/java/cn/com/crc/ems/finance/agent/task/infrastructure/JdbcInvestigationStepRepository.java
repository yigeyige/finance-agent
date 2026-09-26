package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.task.application.InvestigationStepRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;

/** 使用参数化 SQL 访问有序调查步骤。 */
@Repository
public class JdbcInvestigationStepRepository implements InvestigationStepRepository {

    private static final String SELECT = "SELECT step_id, task_id, sequence_no, goal, recommended, status, created_at, "
            + "completed_at, created_by, updated_by, updated_at FROM task.investigation_step ";

    private final JdbcTemplate jdbcTemplate;

    public JdbcInvestigationStepRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<InvestigationStep> findById(UUID stepId) {
        return jdbcTemplate.query(SELECT + "WHERE step_id = ?", (rs, rowNum) -> JdbcTaskMappings.step(rs), stepId)
                .stream().findFirst();
    }

    @Override
    public List<InvestigationStep> findByTaskId(UUID taskId) {
        return jdbcTemplate.query(SELECT + "WHERE task_id = ? ORDER BY sequence_no", (rs, rowNum) -> JdbcTaskMappings.step(rs), taskId);
    }

    @Override
    public void insert(InvestigationStep step) {
        jdbcTemplate.update("INSERT INTO task.investigation_step (step_id, task_id, sequence_no, goal, recommended, status, "
                        + "created_at, completed_at, created_by, updated_by, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                step.stepId(), step.taskId(), step.sequenceNo(), step.goal(), step.recommended(), step.status().name(),
                Timestamp.from(step.createdAt()), timestamp(step.completedAt()), step.createdBy(), step.updatedBy(),
                Timestamp.from(step.updatedAt()));
    }

    private static Timestamp timestamp(java.time.Instant value) {
        return value == null ? null : Timestamp.from(value);
    }
}
