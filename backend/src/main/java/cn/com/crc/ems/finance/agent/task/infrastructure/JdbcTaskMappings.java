package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.ApprovalStatus;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;
import cn.com.crc.ems.finance.agent.task.domain.StepStatus;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;
import cn.com.crc.ems.finance.agent.task.domain.TaskPremise;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 集中维护 task schema 的 ResultSet 到领域对象映射。 */
final class JdbcTaskMappings {

    private JdbcTaskMappings() {
    }

    static InvestigationTask task(ResultSet rs) throws SQLException {
        return new InvestigationTask(uuid(rs, "task_id"), rs.getString("title"), rs.getString("original_question"),
                uuid(rs, "initiator_user_id"), TaskStatus.valueOf(rs.getString("status")),
                uuid(rs, "runtime_bundle_id"), uuid(rs, "current_scope_revision_id"), uuid(rs, "current_step_id"),
                instant(rs, "created_at"), instant(rs, "updated_at"), rs.getLong("row_version"),
                instant(rs, "closed_at"), uuid(rs, "created_by"), uuid(rs, "updated_by"));
    }

    static ScopeRevision scope(ResultSet rs) throws SQLException {
        String schemaVersion = rs.getString("schema_version");
        return new ScopeRevision(uuid(rs, "scope_revision_id"), uuid(rs, "task_id"), rs.getInt("revision_no"),
                rs.getString("system_code"), json(rs, "object_selector", schemaVersion),
                json(rs, "effective_time_range", schemaVersion), json(rs, "included_paths", schemaVersion),
                json(rs, "excluded_paths", schemaVersion), json(rs, "allowed_read_fields", schemaVersion),
                json(rs, "allowed_retain_fields", schemaVersion), json(rs, "allowed_model_fields", schemaVersion),
                uuid(rs, "confirmed_by"), instant(rs, "confirmed_at"), rs.getString("scope_digest"), schemaVersion,
                uuid(rs, "created_by"), instant(rs, "created_at"), uuid(rs, "updated_by"), instant(rs, "updated_at"));
    }

    static TaskPremise premise(ResultSet rs) throws SQLException {
        return new TaskPremise(uuid(rs, "premise_id"), uuid(rs, "task_id"), rs.getString("premise_type"),
                rs.getString("subject_key"), rs.getString("statement"), uuid(rs, "provided_by"),
                rs.getString("source_description"), instant(rs, "provided_at"), rs.getBoolean("independently_verified"),
                uuid(rs, "created_by"), instant(rs, "created_at"), uuid(rs, "updated_by"), instant(rs, "updated_at"));
    }

    static InvestigationStep step(ResultSet rs) throws SQLException {
        return new InvestigationStep(uuid(rs, "step_id"), uuid(rs, "task_id"), rs.getInt("sequence_no"),
                rs.getString("goal"), rs.getBoolean("recommended"), StepStatus.valueOf(rs.getString("status")),
                instant(rs, "created_at"), instant(rs, "completed_at"), uuid(rs, "created_by"), instant(rs, "updated_at"), uuid(rs, "updated_by"));
    }

    static StepApproval approval(ResultSet rs) throws SQLException {
        return new StepApproval(uuid(rs, "approval_id"), uuid(rs, "step_id"), rs.getInt("revision_no"),
                uuid(rs, "approved_by"), instant(rs, "approved_at"), instant(rs, "expires_at"), rs.getString("goal_hash"),
                uuid(rs, "source_binding_id"), uuid(rs, "scope_revision_id"), rs.getString("field_policy_version"),
                rs.getString("query_template_version"), rs.getInt("max_calls"), rs.getInt("used_calls"),
                rs.getString("approval_digest"), ApprovalStatus.valueOf(rs.getString("status")),
                uuid(rs, "created_by"), instant(rs, "created_at"), uuid(rs, "updated_by"), instant(rs, "updated_at"));
    }

    static TaskLease lease(ResultSet rs) throws SQLException {
        return new TaskLease(uuid(rs, "lease_id"), uuid(rs, "task_id"), uuid(rs, "step_id"),
                rs.getString("owner_instance"), instant(rs, "acquired_at"), instant(rs, "heartbeat_at"),
                instant(rs, "expires_at"), instant(rs, "released_at"), uuid(rs, "created_by"),
                instant(rs, "created_at"), uuid(rs, "updated_by"), instant(rs, "updated_at"));
    }

    static ToolInvocation invocation(ResultSet rs) throws SQLException {
        return new ToolInvocation(uuid(rs, "invocation_id"), uuid(rs, "task_id"), uuid(rs, "step_id"),
                uuid(rs, "approval_id"), rs.getString("tool_code"), rs.getString("idempotency_key"),
                rs.getString("request_digest"), rs.getString("response_digest"),
                InvocationStatus.valueOf(rs.getString("status")), instant(rs, "started_at"),
                instant(rs, "completed_at"), rs.getString("error_code"), uuid(rs, "created_by"),
                instant(rs, "created_at"), uuid(rs, "updated_by"), instant(rs, "updated_at"));
    }

    private static VersionedJson json(ResultSet rs, String column, String schemaVersion) throws SQLException {
        return new VersionedJson(schemaVersion, rs.getString(column));
    }

    private static UUID uuid(ResultSet rs, String column) throws SQLException {
        return rs.getObject(column, UUID.class);
    }

    private static Instant instant(ResultSet rs, String column) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(column);
        return timestamp == null ? null : timestamp.toInstant();
    }
}
