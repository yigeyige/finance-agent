package cn.com.crc.ems.finance.agent.task.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;

/** 调查步骤持久化端口。 */
public interface InvestigationStepRepository {

    /** 按步骤 ID 查询步骤。 */
    Optional<InvestigationStep> findById(UUID stepId);

    /** 查询任务的有序步骤。 */
    List<InvestigationStep> findByTaskId(UUID taskId);

    /** 在当前事务中按 PostgreSQL 行锁语义领取下一步。 */
    Optional<InvestigationStep> findNextForUpdateSkipLocked(String taskStatus, String stepStatus);

    /** 追加调查步骤。 */
    void insert(InvestigationStep step);
}
