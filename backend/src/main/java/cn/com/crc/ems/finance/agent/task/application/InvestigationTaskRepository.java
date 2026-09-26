package cn.com.crc.ems.finance.agent.task.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/** 调查任务持久化端口。 */
public interface InvestigationTaskRepository {

    /** 按任务 ID 查询任务。 */
    Optional<InvestigationTask> findById(UUID taskId);

    /** 插入新任务。 */
    void insert(InvestigationTask task);

    /** 按期望行版本更新任务，返回是否成功。 */
    boolean updateWithVersion(InvestigationTask task, long expectedRowVersion);
}
