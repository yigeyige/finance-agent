package cn.com.crc.ems.finance.agent.governance.infrastructure;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.governance.application.RuntimeBundleRepository;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundle;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundleStatus;

/** 使用参数化 SQL 访问受治理的运行版本包。 */
@Repository
public class JdbcRuntimeBundleRepository implements RuntimeBundleRepository {

    private static final String SELECT = "SELECT runtime_bundle_id, application_version, agentscope_version, "
            + "model_binding_version, prompt_bundle_version, rule_set_version, tool_catalog_version, "
            + "source_catalog_version, knowledge_release_id, bundle_digest, status, created_at, created_by, "
            + "updated_at, updated_by FROM governance.runtime_bundle ";

    private final JdbcTemplate jdbcTemplate;

    public JdbcRuntimeBundleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<RuntimeBundle> findById(UUID runtimeBundleId) {
        return findOne(SELECT + "WHERE runtime_bundle_id = ?", runtimeBundleId);
    }

    @Override
    public Optional<RuntimeBundle> findPublishedById(UUID runtimeBundleId) {
        return findOne(SELECT + "WHERE runtime_bundle_id = ? AND status = 'PUBLISHED'", runtimeBundleId);
    }

    @Override
    public void save(RuntimeBundle bundle) {
        jdbcTemplate.update("UPDATE governance.runtime_bundle SET application_version = ?, agentscope_version = ?, "
                        + "model_binding_version = ?, prompt_bundle_version = ?, rule_set_version = ?, "
                        + "tool_catalog_version = ?, source_catalog_version = ?, knowledge_release_id = ?, "
                        + "bundle_digest = ?, status = ?, updated_at = ?, updated_by = ? WHERE runtime_bundle_id = ?",
                bundle.applicationVersion(), bundle.agentscopeVersion(), bundle.modelBindingVersion(),
                bundle.promptBundleVersion(), bundle.ruleSetVersion(), bundle.toolCatalogVersion(),
                bundle.sourceCatalogVersion(), bundle.knowledgeReleaseId(), bundle.bundleDigest(),
                bundle.status().name(), Timestamp.from(bundle.updatedAt()), bundle.updatedBy(), bundle.runtimeBundleId());
    }

    @Override
    public void insertIfAbsent(RuntimeBundle bundle) {
        jdbcTemplate.update("INSERT INTO governance.runtime_bundle "
                        + "(runtime_bundle_id, application_version, agentscope_version, model_binding_version, "
                        + "prompt_bundle_version, rule_set_version, tool_catalog_version, source_catalog_version, "
                        + "knowledge_release_id, bundle_digest, status, created_at, created_by, updated_at, updated_by) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (runtime_bundle_id) DO NOTHING",
                bundle.runtimeBundleId(), bundle.applicationVersion(), bundle.agentscopeVersion(),
                bundle.modelBindingVersion(), bundle.promptBundleVersion(), bundle.ruleSetVersion(),
                bundle.toolCatalogVersion(), bundle.sourceCatalogVersion(), bundle.knowledgeReleaseId(),
                bundle.bundleDigest(), bundle.status().name(), Timestamp.from(bundle.createdAt()), bundle.createdBy(),
                Timestamp.from(bundle.updatedAt()), bundle.updatedBy());
    }

    private Optional<RuntimeBundle> findOne(String sql, UUID runtimeBundleId) {
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new RuntimeBundle(
                        resultSet.getObject("runtime_bundle_id", UUID.class),
                        resultSet.getString("application_version"),
                        resultSet.getString("agentscope_version"),
                        resultSet.getString("model_binding_version"),
                        resultSet.getString("prompt_bundle_version"),
                        resultSet.getString("rule_set_version"),
                        resultSet.getString("tool_catalog_version"),
                        resultSet.getString("source_catalog_version"),
                        resultSet.getObject("knowledge_release_id", UUID.class),
                        resultSet.getString("bundle_digest"),
                        RuntimeBundleStatus.valueOf(resultSet.getString("status")),
                        resultSet.getTimestamp("created_at").toInstant(),
                        resultSet.getObject("created_by", UUID.class),
                        resultSet.getTimestamp("updated_at").toInstant(),
                        resultSet.getObject("updated_by", UUID.class)), runtimeBundleId).stream().findFirst();
    }
}
