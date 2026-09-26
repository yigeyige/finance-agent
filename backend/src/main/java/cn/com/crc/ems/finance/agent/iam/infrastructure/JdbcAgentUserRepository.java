package cn.com.crc.ems.finance.agent.iam.infrastructure;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.iam.application.AgentUserRepository;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUserStatus;

/** 使用参数化 SQL 访问 IAM 身份账户。 */
@Repository
public class JdbcAgentUserRepository implements AgentUserRepository {

    private static final String SELECT = "SELECT user_id, auth_subject, employee_no, display_name, status, "
            + "created_at, created_by, updated_at, updated_by FROM iam.agent_user ";

    private final JdbcTemplate jdbcTemplate;

    public JdbcAgentUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<AgentUser> findById(UUID userId) {
        return findOne(SELECT + "WHERE user_id = ?", userId);
    }

    @Override
    public Optional<AgentUser> findByAuthSubject(String authSubject) {
        return findOne(SELECT + "WHERE auth_subject = ?", authSubject);
    }

    @Override
    public Optional<AgentUser> findByEmployeeNo(String employeeNo) {
        return findOne(SELECT + "WHERE employee_no = ?", employeeNo);
    }

    @Override
    public void save(AgentUser user) {
        jdbcTemplate.update("UPDATE iam.agent_user SET auth_subject = ?, employee_no = ?, display_name = ?, "
                        + "status = ?, updated_at = ?, updated_by = ? WHERE user_id = ?",
                user.authSubject(), user.employeeNo(), user.displayName(), user.status().name(),
                Timestamp.from(user.updatedAt()), user.updatedBy(), user.userId());
    }

    @Override
    public void insertIfAbsent(AgentUser user) {
        jdbcTemplate.update("INSERT INTO iam.agent_user "
                        + "(user_id, auth_subject, employee_no, display_name, status, created_at, created_by, updated_at, updated_by) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (user_id) DO NOTHING",
                user.userId(), user.authSubject(), user.employeeNo(), user.displayName(), user.status().name(),
                Timestamp.from(user.createdAt()), user.createdBy(), Timestamp.from(user.updatedAt()), user.updatedBy());
    }

    private Optional<AgentUser> findOne(String sql, Object parameter) {
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new AgentUser(
                        resultSet.getObject("user_id", UUID.class),
                        resultSet.getString("auth_subject"),
                        resultSet.getString("employee_no"),
                        resultSet.getString("display_name"),
                        AgentUserStatus.valueOf(resultSet.getString("status")),
                        toInstant(resultSet.getTimestamp("created_at")),
                        resultSet.getObject("created_by", UUID.class),
                        toInstant(resultSet.getTimestamp("updated_at")),
                        resultSet.getObject("updated_by", UUID.class)),
                parameter).stream().findFirst();
    }

    private static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
