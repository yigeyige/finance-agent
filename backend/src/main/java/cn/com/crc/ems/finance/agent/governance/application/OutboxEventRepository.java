package cn.com.crc.ems.finance.agent.governance.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.governance.domain.OutboxEvent;

/** 本地 outbox 事件持久化端口。 */
public interface OutboxEventRepository {

    /** 按幂等键查询既有事件。 */
    Optional<OutboxEvent> findByIdempotencyKey(String idempotencyKey);

    /** 查询待投递且未超过尝试上限的事件。 */
    List<OutboxEvent> findPending(int limit, int maxAttempts);

    /** 幂等插入 outbox 事件。 */
    OutboxEvent insertIfAbsent(OutboxEvent event);

    /** 记录投递尝试次数。 */
    void recordDeliveryAttempt(UUID outboxId, Instant updatedAt);

    /** 标记事件已投递。 */
    void markDelivered(UUID outboxId, Instant deliveredAt, UUID updatedBy, Instant updatedAt);
}
