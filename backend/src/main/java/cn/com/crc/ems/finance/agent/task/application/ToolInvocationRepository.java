package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;

/**
 *
 * @Description: 工具调用意图和结果持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface ToolInvocationRepository {

    /** 按幂等键查询原调用。 */
    Optional<ToolInvocation> findByIdempotencyKey(String idempotencyKey);

    /** 查询任务最近一次工具调用摘要，供诊断只读汇总使用。 */
    default Optional<ToolInvocation> findLatestByTaskId(UUID taskId) {
        return Optional.empty();
    }

    /** 追加调用意图。 */
    void insert(ToolInvocation invocation);

    /** 更新调用状态和结果摘要。 */
    boolean updateStatus(String idempotencyKey, InvocationStatus status, String responseDigest,
            String errorCode, Instant completedAt, Instant updatedAt, java.util.UUID actorId);

    /** 仅当数据库中的状态仍为 expectedStatus 时推进状态，防止覆盖并发终态。 */
    default boolean updateStatusIfCurrent(String idempotencyKey, InvocationStatus expectedStatus,
            InvocationStatus targetStatus, String responseDigest, String errorCode, Instant completedAt,
            Instant updatedAt, java.util.UUID actorId) {
        return updateStatus(idempotencyKey, targetStatus, responseDigest, errorCode, completedAt, updatedAt, actorId);
    }
}
