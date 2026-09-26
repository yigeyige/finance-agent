package cn.com.crc.ems.finance.agent.governance.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundle;

/** 运行版本包持久化端口。 */
public interface RuntimeBundleRepository {

    /** 按主键查询运行版本包。 */
    Optional<RuntimeBundle> findById(UUID runtimeBundleId);

    /** 仅查询可供任务运行的已发布版本包。 */
    Optional<RuntimeBundle> findPublishedById(UUID runtimeBundleId);

    /** 更新运行版本包及其审计字段。 */
    void save(RuntimeBundle bundle);

    /** 幂等插入初始化运行版本包。 */
    void insertIfAbsent(RuntimeBundle bundle);
}
