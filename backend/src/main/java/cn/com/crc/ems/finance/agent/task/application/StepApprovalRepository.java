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

    /** 将任务旧范围下仍生效的审批标记为 stale。 */
    int markStaleByScopeRevision(UUID taskId, UUID scopeRevisionId, UUID actorUserId,
            java.time.Instant updatedAt);
}
