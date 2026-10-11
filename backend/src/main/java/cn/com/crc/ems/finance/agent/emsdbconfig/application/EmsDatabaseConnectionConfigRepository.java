package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;

/** EMS 数据库连接配置的持久化端口。 */
public interface EmsDatabaseConnectionConfigRepository {
    List<EmsDatabaseConnectionConfig> findAll();
    Optional<EmsDatabaseConnectionConfig> findById(UUID configId);
    Optional<EmsDatabaseConnectionConfig> findEnabled();
    void insert(EmsDatabaseConnectionConfig config);
    boolean update(EmsDatabaseConnectionConfig config);
    void disableAll(UUID operatorId, Instant updatedAt);
}
