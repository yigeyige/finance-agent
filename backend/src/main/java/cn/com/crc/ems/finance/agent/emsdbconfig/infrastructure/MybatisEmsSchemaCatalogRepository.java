package cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsSchemaCatalog;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsSchemaCatalogRepository;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsSchemaTable;
import cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure.mapper.EmsSchemaCatalogMapper;
import cn.com.crc.ems.finance.agent.shared.domain.JsonCodec;

/**
 * @Description: 使用 MyBatis 持久化单实例完整结构快照，保留空目录与未同步的区别。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 20:00:00
 */
@Repository
public class MybatisEmsSchemaCatalogRepository implements EmsSchemaCatalogRepository {
    private final EmsSchemaCatalogMapper mapper;

    public MybatisEmsSchemaCatalogRepository(EmsSchemaCatalogMapper mapper) { this.mapper = mapper; }

    @Override
    public Optional<EmsSchemaCatalog> find(UUID configId) {
        String json = mapper.findJson(configId);
        if (json == null) return Optional.empty();
        try {
            return Optional.of(new EmsSchemaCatalog(Arrays.asList(JsonCodec.mapper().readValue(json, EmsSchemaTable[].class))));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("数据库实例结构目录无法读取", exception);
        }
    }

    @Override
    public Optional<SyncStatus> status(UUID configId) {
        Integer count = mapper.findTableCount(configId);
        if (count == null) return Optional.empty();
        return Optional.of(new SyncStatus(count, mapper.findSyncedAt(configId)));
    }

    @Override
    public void save(UUID configId, EmsSchemaCatalog catalog, Instant syncedAt) {
        try {
            mapper.save(configId, JsonCodec.mapper().writeValueAsString(catalog.tables()), catalog.tables().size(), syncedAt);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("数据库实例结构目录无法保存", exception);
        }
    }
}
