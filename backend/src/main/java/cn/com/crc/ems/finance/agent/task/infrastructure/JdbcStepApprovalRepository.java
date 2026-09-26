package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.task.application.StepApprovalRepository;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;

/** 使用参数化 SQL 访问步骤审批历史。 */
@Repository
public class JdbcStepApprovalRepository implements StepApprovalRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStepApprovalRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<StepApproval> findByStepId(UUID stepId) {
        return jdbcTemplate.query("SELECT approval_id, step_id, revision_no, approved_by, approved_at, expires_at, goal_hash, "
                        + "source_binding_id, scope_revision_id, field_policy_version, query_template_version, max_calls, used_calls, "
                        + "approval_digest, status, created_by, created_at, updated_by, updated_at FROM task.step_approval "
                        + "WHERE step_id = ? ORDER BY revision_no",
                (rs, rowNum) -> JdbcTaskMappings.approval(rs), stepId);
    }

    @Override
    public void insert(StepApproval approval) {
        jdbcTemplate.update("INSERT INTO task.step_approval (approval_id, step_id, revision_no, approved_by, approved_at, expires_at, "
                        + "goal_hash, source_binding_id, scope_revision_id, field_policy_version, query_template_version, max_calls, "
                        + "used_calls, approval_digest, status, created_by, created_at, updated_by, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", approval.approvalId(), approval.stepId(),
                approval.revisionNo(), approval.approvedBy(), Timestamp.from(approval.approvedAt()), timestamp(approval.expiresAt()),
                approval.goalHash(), approval.sourceBindingId(), approval.scopeRevisionId(), approval.fieldPolicyVersion(),
                approval.queryTemplateVersion(), approval.maxCalls(), approval.usedCalls(), approval.approvalDigest(), approval.status().name(),
                approval.createdBy(), Timestamp.from(approval.createdAt()), approval.updatedBy(), Timestamp.from(approval.updatedAt()));
    }

    private static Timestamp timestamp(java.time.Instant value) {
        return value == null ? null : Timestamp.from(value);
    }
}
