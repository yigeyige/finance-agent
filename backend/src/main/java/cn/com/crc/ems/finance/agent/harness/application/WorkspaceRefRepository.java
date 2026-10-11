package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.harness.domain.WorkspaceRef;

/**
 *
 * @Description: 按 task/user/session 隔离 workspace 引用的持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface WorkspaceRefRepository {

    /** 只按完整业务边界查询 workspace 引用。 */
    Optional<WorkspaceRef> findById(UUID workspaceRefId, UUID taskId, UUID userId);

    /** 查询指定 session 的 workspace 引用。 */
    List<WorkspaceRef> findBySessionId(UUID sessionId, UUID taskId, UUID userId);

    /** 追加 workspace 引用。 */
    void insert(WorkspaceRef workspaceRef);
}
