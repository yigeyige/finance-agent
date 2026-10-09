package cn.com.crc.ems.finance.agent.modelconfig.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.modelconfig.domain.ModelConnectionConfig;

/** 管理模型连接配置的持久化端口。 */
public interface ModelConnectionConfigRepository {
    List<ModelConnectionConfig> findAll();
    Optional<ModelConnectionConfig> findById(UUID configId);
    Optional<ModelConnectionConfig> findEnabled();
    void insert(ModelConnectionConfig config);
    boolean update(ModelConnectionConfig config);
    void disableAll(UUID operatorId, java.time.Instant updatedAt);
}
