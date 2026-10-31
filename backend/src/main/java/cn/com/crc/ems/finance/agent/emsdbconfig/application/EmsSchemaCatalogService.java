package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

/**
 * @Description: 编排管理员手动同步实例结构目录及成功快照状态读取。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 20:00:00
 */
@Service
public class EmsSchemaCatalogService {
    private final EmsDatabaseConnectionConfigService configService;
    private final EmsDatabaseQueryExecutor executor;
    private final EmsSchemaCatalogRepository repository;
    private final Clock clock;

    public EmsSchemaCatalogService(EmsDatabaseConnectionConfigService configService, EmsDatabaseQueryExecutor executor,
            EmsSchemaCatalogRepository repository, Clock clock) {
        this.configService = configService;
        this.executor = executor;
        this.repository = repository;
        this.clock = clock;
    }

    /** 先完整读取源库，再以单条本地写入原子替换成功快照。 */
    public EmsSchemaCatalogRepository.SyncStatus sync(UUID configId) {
        var config = configService.requireConfigured(configId);
        try {
            EmsSchemaCatalog catalog = executor.listConfigurationSchema(config);
            var now = clock.instant();
            repository.save(configId, catalog, now);
            return new EmsSchemaCatalogRepository.SyncStatus(catalog.tables().size(), now);
        } catch (RuntimeException exception) {
            throw new EmsSchemaSyncException(exception);
        }
    }

    public Optional<EmsSchemaCatalogRepository.SyncStatus> status(UUID configId) { return repository.status(configId); }
    public Optional<EmsSchemaCatalog> find(UUID configId) { return repository.find(configId); }

    /**
     * @description: 读取指定已配置实例的本地结构快照，供管理员查看，不连接 EMS 源库。
     * @param: configId 数据库实例标识
     * @return: 已同步的结构快照；从未同步时为空
     * @author: yigeyige99
     */
    public Optional<EmsSchemaCatalog> view(UUID configId) {
        configService.requireConfigured(configId);
        return repository.find(configId);
    }
}
