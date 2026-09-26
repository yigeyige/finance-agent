package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.task.application.ScopeRevisionRepository;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;

/** 使用参数化 SQL 追加和查询不可变范围修订。 */
@Repository
public class JdbcScopeRevisionRepository implements ScopeRevisionRepository {

    private static final String SELECT = "SELECT scope_revision_id, task_id, revision_no, system_code, object_selector, "
            + "effective_time_range, included_paths, excluded_paths, allowed_read_fields, allowed_retain_fields, "
            + "allowed_model_fields, confirmed_by, confirmed_at, scope_digest, schema_version, created_by, created_at, "
            + "updated_by, updated_at FROM task.scope_revision ";

    private final JdbcTemplate jdbcTemplate;

    public JdbcScopeRevisionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<ScopeRevision> findById(UUID scopeRevisionId) {
        return jdbcTemplate.query(SELECT + "WHERE scope_revision_id = ?", (rs, rowNum) -> JdbcTaskMappings.scope(rs), scopeRevisionId)
                .stream().findFirst();
    }

    @Override
    public Optional<ScopeRevision> findLatestByTaskId(UUID taskId) {
        return jdbcTemplate.query(SELECT + "WHERE task_id = ? ORDER BY revision_no DESC LIMIT 1",
                (rs, rowNum) -> JdbcTaskMappings.scope(rs), taskId).stream().findFirst();
    }

    @Override
    public void insert(ScopeRevision revision) {
        SqlParameterValue objectSelector = json(revision.objectSelector().json());
        SqlParameterValue effectiveTimeRange = json(revision.effectiveTimeRange().json());
        SqlParameterValue includedPaths = json(revision.includedPaths().json());
        SqlParameterValue excludedPaths = json(revision.excludedPaths().json());
        SqlParameterValue allowedReadFields = json(revision.allowedReadFields().json());
        SqlParameterValue allowedRetainFields = json(revision.allowedRetainFields().json());
        SqlParameterValue allowedModelFields = json(revision.allowedModelFields().json());
        jdbcTemplate.update("INSERT INTO task.scope_revision (scope_revision_id, task_id, revision_no, system_code, "
                        + "object_selector, effective_time_range, included_paths, excluded_paths, allowed_read_fields, "
                        + "allowed_retain_fields, allowed_model_fields, confirmed_by, confirmed_at, scope_digest, schema_version, "
                        + "created_by, created_at, updated_by, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                revision.scopeRevisionId(), revision.taskId(), revision.revisionNo(), revision.systemCode(), objectSelector,
                effectiveTimeRange, includedPaths, excludedPaths, allowedReadFields, allowedRetainFields, allowedModelFields,
                revision.confirmedBy(), Timestamp.from(revision.confirmedAt()), revision.scopeDigest(), revision.schemaVersion(),
                revision.createdBy(), Timestamp.from(revision.createdAt()), revision.updatedBy(), Timestamp.from(revision.updatedAt()));
    }

    private static SqlParameterValue json(String value) {
        return new SqlParameterValue(Types.OTHER, value);
    }
}
