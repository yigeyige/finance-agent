package cn.com.crc.ems.finance.agent.iam.infrastructure;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.iam.application.RoleBindingRepository;
import cn.com.crc.ems.finance.agent.iam.domain.RoleBinding;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/** 使用参数化 SQL 访问可撤销的角色授权关系。 */
@Repository
public class JdbcRoleBindingRepository implements RoleBindingRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcRoleBindingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RoleBinding> findByUserId(UUID userId) {
        return jdbcTemplate.query("SELECT user_id, role_code, granted_by, granted_at, revoked_at, created_by, "
                        + "created_at, updated_by, updated_at FROM iam.role_binding WHERE user_id = ? ORDER BY granted_at",
                (resultSet, rowNum) -> new RoleBinding(
                        resultSet.getObject("user_id", UUID.class),
                        RoleCode.valueOf(resultSet.getString("role_code")),
                        resultSet.getObject("granted_by", UUID.class),
                        resultSet.getTimestamp("granted_at").toInstant(),
                        toInstant(resultSet.getTimestamp("revoked_at")),
                        resultSet.getObject("created_by", UUID.class),
                        resultSet.getTimestamp("created_at").toInstant(),
                        resultSet.getObject("updated_by", UUID.class),
                        resultSet.getTimestamp("updated_at").toInstant()), userId);
    }

    @Override
    public void grant(RoleBinding binding) {
        jdbcTemplate.update("INSERT INTO iam.role_binding "
                        + "(user_id, role_code, granted_by, granted_at, revoked_at, created_by, created_at, updated_by, updated_at) "
                        + "SELECT ?, ?, ?, ?, ?, ?, ?, ?, ? WHERE NOT EXISTS ("
                        + "SELECT 1 FROM iam.role_binding WHERE user_id = ? AND role_code = ? AND revoked_at IS NULL)",
                binding.userId(), binding.roleCode().name(), binding.grantedBy(), Timestamp.from(binding.grantedAt()),
                binding.revokedAt() == null ? null : Timestamp.from(binding.revokedAt()), binding.createdBy(),
                Timestamp.from(binding.createdAt()), binding.updatedBy(), Timestamp.from(binding.updatedAt()),
                binding.userId(), binding.roleCode().name());
    }

    @Override
    public void revoke(UUID userId, RoleCode roleCode, Instant revokedAt, UUID actorId) {
        jdbcTemplate.update("UPDATE iam.role_binding SET revoked_at = ?, updated_by = ?, updated_at = ? "
                        + "WHERE user_id = ? AND role_code = ? AND revoked_at IS NULL",
                Timestamp.from(revokedAt), actorId, Timestamp.from(revokedAt), userId, roleCode.name());
    }

    private static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
