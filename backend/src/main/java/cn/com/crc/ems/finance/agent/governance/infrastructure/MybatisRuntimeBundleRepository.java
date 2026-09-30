package cn.com.crc.ems.finance.agent.governance.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.governance.application.RuntimeBundleRepository;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundle;
import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.RuntimeBundleEntity;
import cn.com.crc.ems.finance.agent.governance.infrastructure.mapper.RuntimeBundleMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/** 使用 MyBatis-Plus Mapper 访问受治理的运行版本包。 */
@Repository
public class MybatisRuntimeBundleRepository implements RuntimeBundleRepository {

    private final RuntimeBundleMapper mapper;

    public MybatisRuntimeBundleRepository(RuntimeBundleMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<RuntimeBundle> findById(UUID runtimeBundleId) {
        return Optional.ofNullable(mapper.selectById(PersistenceUuid.toStorage(runtimeBundleId)))
                .map(MybatisRuntimeBundleRepository::toDomain);
    }

    @Override
    public Optional<RuntimeBundle> findPublishedById(UUID runtimeBundleId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<RuntimeBundleEntity>()
                        .eq("runtime_bundle_id", runtimeBundleId).eq("status", "PUBLISHED")))
                .map(MybatisRuntimeBundleRepository::toDomain);
    }

    @Override
    public void save(RuntimeBundle bundle) {
        mapper.updateById(toEntity(bundle));
    }

    @Override
    public void insertIfAbsent(RuntimeBundle bundle) {
        if (mapper.selectById(PersistenceUuid.toStorage(bundle.runtimeBundleId())) == null) {
            mapper.insert(toEntity(bundle));
        }
    }

    private static RuntimeBundle toDomain(RuntimeBundleEntity entity) {
        return new RuntimeBundle(PersistenceUuid.toDomain(entity.runtimeBundleId), entity.applicationVersion, entity.agentscopeVersion,
                entity.modelBindingVersion, entity.promptBundleVersion, entity.ruleSetVersion, entity.toolCatalogVersion,
                entity.sourceCatalogVersion, entity.knowledgeReleaseId, entity.bundleDigest,
                cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundleStatus.valueOf(entity.status),
                entity.createdAt, entity.createdBy, entity.updatedAt, entity.updatedBy);
    }

    private static RuntimeBundleEntity toEntity(RuntimeBundle bundle) {
        RuntimeBundleEntity entity = new RuntimeBundleEntity();
        entity.runtimeBundleId = PersistenceUuid.toStorage(bundle.runtimeBundleId());
        entity.applicationVersion = bundle.applicationVersion();
        entity.agentscopeVersion = bundle.agentscopeVersion();
        entity.modelBindingVersion = bundle.modelBindingVersion();
        entity.promptBundleVersion = bundle.promptBundleVersion();
        entity.ruleSetVersion = bundle.ruleSetVersion();
        entity.toolCatalogVersion = bundle.toolCatalogVersion();
        entity.sourceCatalogVersion = bundle.sourceCatalogVersion();
        entity.knowledgeReleaseId = bundle.knowledgeReleaseId();
        entity.bundleDigest = bundle.bundleDigest();
        entity.status = bundle.status().name();
        entity.createdAt = bundle.createdAt();
        entity.createdBy = bundle.createdBy();
        entity.updatedAt = bundle.updatedAt();
        entity.updatedBy = bundle.updatedBy();
        return entity;
    }
}
