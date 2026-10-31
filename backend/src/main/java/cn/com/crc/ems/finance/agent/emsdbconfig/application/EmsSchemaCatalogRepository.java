package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @Description: 数据库实例结构目录成功快照的持久化端口。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 20:00:00
 */
public interface EmsSchemaCatalogRepository {
    Optional<EmsSchemaCatalog> find(UUID configId);
    Optional<SyncStatus> status(UUID configId);
    void save(UUID configId, EmsSchemaCatalog catalog, Instant syncedAt);

    record SyncStatus(int tableCount, Instant syncedAt) { }
}
