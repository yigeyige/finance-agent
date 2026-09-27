package cn.com.crc.ems.finance.agent.governance.domain;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 本地事务 outbox 事件，只保存安全 payload 和投递状态。 */
public record OutboxEvent(UUID outboxId, String aggregateType, String aggregateId, String eventType,
        String idempotencyKey, VersionedJson payload, Instant createdAt, Instant deliveredAt,
        int deliveryAttempts, UUID createdBy, UUID updatedBy, Instant updatedAt) {

    public OutboxEvent {
        requireText(aggregateType, "aggregateType");
        requireText(aggregateId, "aggregateId");
        requireText(eventType, "eventType");
        requireText(idempotencyKey, "idempotencyKey");
        if (outboxId == null || createdAt == null || updatedAt == null || deliveryAttempts < 0) {
            throw new IllegalArgumentException("outbox id, timestamps and deliveryAttempts are invalid");
        }
        payload = AuditDigest.canonicalize(payload);
    }

    /** 记录一次投递尝试但不改变事件身份和 payload。 */
    public OutboxEvent recordDeliveryAttempt(Instant at) {
        if (at == null) {
            throw new IllegalArgumentException("delivery attempt time is required");
        }
        return new OutboxEvent(outboxId, aggregateType, aggregateId, eventType, idempotencyKey, payload, createdAt,
                deliveredAt, deliveryAttempts + 1, createdBy, updatedBy, at);
    }

    /** 标记事件已投递。 */
    public OutboxEvent markDelivered(Instant at) {
        if (at == null) {
            throw new IllegalArgumentException("delivery time is required");
        }
        return new OutboxEvent(outboxId, aggregateType, aggregateId, eventType, idempotencyKey, payload, createdAt,
                at, deliveryAttempts, createdBy, updatedBy, at);
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
    }
}
