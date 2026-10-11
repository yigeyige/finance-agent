package cn.com.crc.ems.finance.agent.modelconfig.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import cn.com.crc.ems.finance.agent.modelconfig.application.ModelConnectionConfigRepository;
import cn.com.crc.ems.finance.agent.modelconfig.domain.ModelConnectionConfig;
import cn.com.crc.ems.finance.agent.modelconfig.infrastructure.entity.ModelConnectionConfigEntity;
import cn.com.crc.ems.finance.agent.modelconfig.infrastructure.mapper.ModelConnectionConfigMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/**
 *
 * @Description: 使用 MyBatis-Plus 查询与切换大模型连接。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisModelConnectionConfigRepository implements ModelConnectionConfigRepository {
    private final ModelConnectionConfigMapper mapper;
    public MybatisModelConnectionConfigRepository(ModelConnectionConfigMapper mapper) { this.mapper = mapper; }
    @Override public List<ModelConnectionConfig> findAll() { return mapper.selectList(new QueryWrapper<ModelConnectionConfigEntity>().orderByDesc("updated_at")).stream().map(MybatisModelConnectionConfigRepository::toDomain).toList(); }
    @Override public Optional<ModelConnectionConfig> findById(UUID configId) { return Optional.ofNullable(mapper.selectById(PersistenceUuid.toStorage(configId))).map(MybatisModelConnectionConfigRepository::toDomain); }
    @Override public Optional<ModelConnectionConfig> findEnabled() { return Optional.ofNullable(mapper.selectOne(new QueryWrapper<ModelConnectionConfigEntity>().eq("enabled", true))).map(MybatisModelConnectionConfigRepository::toDomain); }
    @Override public void insert(ModelConnectionConfig config) { mapper.insert(toEntity(config)); }
    @Override public boolean update(ModelConnectionConfig config) { return mapper.updateById(toEntity(config)) == 1; }
    @Override public void disableAll(UUID operatorId, java.time.Instant updatedAt) { mapper.update(null, new UpdateWrapper<ModelConnectionConfigEntity>().eq("enabled", true).set("enabled", false).set("updated_by", operatorId).set("updated_at", updatedAt)); }
    private static ModelConnectionConfig toDomain(ModelConnectionConfigEntity value) { return new ModelConnectionConfig(PersistenceUuid.toDomain(value.configId), value.name, value.provider, value.apiUrl, value.modelName, value.reasoningEffort, value.apiKeyCiphertext, value.enabled, value.createdBy, value.createdAt, value.updatedBy, value.updatedAt); }
    private static ModelConnectionConfigEntity toEntity(ModelConnectionConfig value) { ModelConnectionConfigEntity entity = new ModelConnectionConfigEntity(); entity.configId = PersistenceUuid.toStorage(value.configId()); entity.name = value.name(); entity.provider = value.provider(); entity.apiUrl = value.apiUrl(); entity.modelName = value.modelName(); entity.reasoningEffort = value.reasoningEffort(); entity.apiKeyCiphertext = value.apiKeyCiphertext(); entity.enabled = value.enabled(); entity.createdBy = value.createdBy(); entity.createdAt = value.createdAt(); entity.updatedBy = value.updatedBy(); entity.updatedAt = value.updatedAt(); return entity; }
}
