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

    /** 按 AgentScope 的 user/session 定位已绑定业务任务。 */
    default Optional<SessionState> findBySessionIdAndUserId(UUID sessionId, UUID userId) {
        throw new UnsupportedOperationException("session binding lookup is not implemented");
    }

    /** 新增 session 状态。 */
    void insert(SessionState sessionState);

    /** 在同一 task/user/session 边界内更新状态引用。 */
    boolean updateState(UUID sessionId, UUID taskId, UUID userId, StateReference stateRef,
            UUID updatedBy, Instant updatedAt);

    /** 删除指定业务边界下的状态；生产适配器默认不允许隐式删除历史。 */
    default boolean delete(UUID sessionId, UUID taskId, UUID userId) {
        throw new UnsupportedOperationException("session state deletion is not implemented");
    }

    /** 返回指定用户可见的已持久化 session ID。 */
    default java.util.Set<UUID> listSessionIds(UUID userId) {
        throw new UnsupportedOperationException("session listing is not implemented");
    }
}
