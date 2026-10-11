package cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigRepository;
import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure.entity.EmsDatabaseConnectionConfigEntity;
import cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure.mapper.EmsDatabaseConnectionConfigMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/**
 *
 * @Description: 使用 MyBatis-Plus 保存 EMS 数据库连接配置。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisEmsDatabaseConnectionConfigRepository implements EmsDatabaseConnectionConfigRepository {
    private final EmsDatabaseConnectionConfigMapper mapper;

    public MybatisEmsDatabaseConnectionConfigRepository(EmsDatabaseConnectionConfigMapper mapper) { this.mapper = mapper; }
    @Override public List<EmsDatabaseConnectionConfig> findAll() { return mapper.selectList(new QueryWrapper<EmsDatabaseConnectionConfigEntity>().orderByDesc("updated_at")).stream().map(MybatisEmsDatabaseConnectionConfigRepository::toDomain).toList(); }
    @Override public Optional<EmsDatabaseConnectionConfig> findById(UUID configId) { return Optional.ofNullable(mapper.selectById(PersistenceUuid.toStorage(configId))).map(MybatisEmsDatabaseConnectionConfigRepository::toDomain); }
    @Override public Optional<EmsDatabaseConnectionConfig> findEnabled() { return Optional.ofNullable(mapper.selectOne(new QueryWrapper<EmsDatabaseConnectionConfigEntity>().eq("enabled", true))).map(MybatisEmsDatabaseConnectionConfigRepository::toDomain); }
    @Override public void insert(EmsDatabaseConnectionConfig config) { mapper.insert(toEntity(config)); }
    @Override public boolean update(EmsDatabaseConnectionConfig config) { return mapper.updateById(toEntity(config)) == 1; }
    @Override public void disableAll(UUID operatorId, Instant updatedAt) { mapper.update(null, new UpdateWrapper<EmsDatabaseConnectionConfigEntity>().eq("enabled", true).set("enabled", false).set("updated_by", operatorId).set("updated_at", updatedAt)); }
    private static EmsDatabaseConnectionConfig toDomain(EmsDatabaseConnectionConfigEntity value) { return new EmsDatabaseConnectionConfig(PersistenceUuid.toDomain(value.configId), value.name, value.jdbcUrl, value.username, value.passwordCiphertext, value.connectTimeoutSeconds, value.queryTimeoutSeconds, value.maxRows, value.enabled, value.createdBy, value.createdAt, value.updatedBy, value.updatedAt); }
    private static EmsDatabaseConnectionConfigEntity toEntity(EmsDatabaseConnectionConfig value) { EmsDatabaseConnectionConfigEntity entity = new EmsDatabaseConnectionConfigEntity(); entity.configId = PersistenceUuid.toStorage(value.configId()); entity.name = value.name(); entity.jdbcUrl = value.jdbcUrl(); entity.username = value.username(); entity.passwordCiphertext = value.passwordCiphertext(); entity.connectTimeoutSeconds = value.connectTimeoutSeconds(); entity.queryTimeoutSeconds = value.queryTimeoutSeconds(); entity.maxRows = value.maxRows(); entity.enabled = value.enabled(); entity.createdBy = value.createdBy(); entity.createdAt = value.createdAt(); entity.updatedBy = value.updatedBy(); entity.updatedAt = value.updatedAt(); return entity; }
}
