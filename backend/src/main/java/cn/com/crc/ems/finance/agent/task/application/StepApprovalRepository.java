package cn.com.crc.ems.finance.agent.task.application;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.StepApproval;

/** 步骤审批持久化端口。 */
public interface StepApprovalRepository {

    /** 查询步骤的审批历史。 */
    List<StepApproval> findByStepId(UUID stepId);

    /** 追加步骤审批。 */
    void insert(StepApproval approval);
}
