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

    /** 追加调查步骤。 */
    void insert(InvestigationStep step);
}
