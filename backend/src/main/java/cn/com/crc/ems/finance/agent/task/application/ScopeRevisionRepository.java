package cn.com.crc.ems.finance.agent.task.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;

/** 只追加任务范围修订的持久化端口。 */
public interface ScopeRevisionRepository {

    /** 按修订 ID 查询范围。 */
    Optional<ScopeRevision> findById(UUID scopeRevisionId);

    /** 查询任务最新范围修订。 */
    Optional<ScopeRevision> findLatestByTaskId(UUID taskId);

    /** 追加新的范围修订，不提供更新操作。 */
    void insert(ScopeRevision revision);
}
