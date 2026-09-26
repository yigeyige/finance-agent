package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.task.application.TaskPremiseRepository;
import cn.com.crc.ems.finance.agent.task.domain.TaskPremise;

/** 使用参数化 SQL 访问任务前提。 */
@Repository
public class JdbcTaskPremiseRepository implements TaskPremiseRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTaskPremiseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(TaskPremise premise) {
        jdbcTemplate.update("INSERT INTO task.task_premise (premise_id, task_id, premise_type, subject_key, statement, "
                        + "provided_by, source_description, provided_at, independently_verified, created_by, created_at, updated_by, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", premise.premiseId(), premise.taskId(),
                premise.premiseType(), premise.subjectKey(), premise.statement(), premise.providedBy(), premise.sourceDescription(),
                Timestamp.from(premise.providedAt()), premise.independentlyVerified(), premise.createdBy(),
                Timestamp.from(premise.createdAt()), premise.updatedBy(), Timestamp.from(premise.updatedAt()));
    }

    @Override
    public List<TaskPremise> findByTaskId(UUID taskId) {
        return jdbcTemplate.query("SELECT premise_id, task_id, premise_type, subject_key, statement, provided_by, "
                        + "source_description, provided_at, independently_verified, created_by, created_at, updated_by, updated_at "
                        + "FROM task.task_premise WHERE task_id = ? ORDER BY provided_at, premise_id",
                (rs, rowNum) -> JdbcTaskMappings.premise(rs), taskId);
    }
}
