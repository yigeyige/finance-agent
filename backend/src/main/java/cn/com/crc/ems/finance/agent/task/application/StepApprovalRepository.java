package cn.com.crc.ems.finance.agent.task.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.StepApproval;

/**
 *
 * @Description: 步骤审批持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface StepApprovalRepository {

    /** 查询步骤的审批历史。 */
    List<StepApproval> findByStepId(UUID stepId);

    /** 按审批标识查询审批。 */
    Optional<StepApproval> findById(UUID approvalId);

    /** 追加步骤审批。 */
    void insert(StepApproval approval);

    /** 将任务旧范围下仍生效的审批标记为 stale。 */
    int markStaleByScopeRevision(UUID taskId, UUID scopeRevisionId, UUID actorUserId,
            java.time.Instant updatedAt);

    /** 撤销活动审批。 */
    int revoke(UUID approvalId, UUID actorUserId, java.time.Instant updatedAt);
}
