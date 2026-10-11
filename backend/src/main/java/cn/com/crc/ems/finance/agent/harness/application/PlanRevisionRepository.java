package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.harness.domain.PlanRevision;

/**
 *
 * @Description: 按 task/user/session 隔离、只追加 Harness 计划修订的持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface PlanRevisionRepository {

    /** 查询指定 session 的计划修订。 */
    List<PlanRevision> findBySessionId(UUID sessionId, UUID taskId, UUID userId);

    /** 追加计划修订。 */
    void insert(PlanRevision planRevision);
}
