package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.harness.domain.SessionState;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;

/** 按 task/user/session 隔离 Harness session 状态的持久化端口。 */
public interface SessionStateRepository {

    /** 只按完整业务边界查询 session。 */
    Optional<SessionState> findById(UUID sessionId, UUID taskId, UUID userId);

    /** 新增 session 状态。 */
    void insert(SessionState sessionState);

    /** 在同一 task/user/session 边界内更新状态引用。 */
    boolean updateState(UUID sessionId, UUID taskId, UUID userId, StateReference stateRef,
            UUID updatedBy, Instant updatedAt);
}
