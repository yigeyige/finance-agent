package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.Optional;

import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;

/** 工具调用意图和结果持久化端口。 */
public interface ToolInvocationRepository {

    /** 按幂等键查询原调用。 */
    Optional<ToolInvocation> findByIdempotencyKey(String idempotencyKey);

    /** 追加调用意图。 */
    void insert(ToolInvocation invocation);

    /** 更新调用状态和结果摘要。 */
    boolean updateStatus(String idempotencyKey, InvocationStatus status, String responseDigest,
            String errorCode, Instant completedAt, Instant updatedAt, java.util.UUID actorId);
}
