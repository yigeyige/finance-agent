package cn.com.crc.ems.finance.agent.task.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/** 调查任务持久化端口。 */
public interface InvestigationTaskRepository {

    /** 按任务 ID 查询任务。 */
    Optional<InvestigationTask> findById(UUID taskId);

    /** 在当前写事务中按任务 ID 加行锁。 */
    Optional<InvestigationTask> findByIdForUpdate(UUID taskId);

    /** 插入新任务。 */
    void insert(InvestigationTask task);

    /** 按期望行版本更新任务，返回是否成功。 */
    boolean updateWithVersion(InvestigationTask task, long expectedRowVersion);

    /** 按期望行版本更新任务，冲突时抛出明确异常。 */
    default void updateWithVersionOrThrow(InvestigationTask task, long expectedRowVersion) {
        if (!updateWithVersion(task, expectedRowVersion)) {
            throw new cn.com.crc.ems.finance.agent.shared.infrastructure.ConcurrentRepositoryOperationException(
                    "investigation task version changed");
        }
    }
}
