package cn.com.crc.ems.finance.agent.governance.infrastructure;

import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.governance.domain.OutboxEvent;
import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.AuditEventEntity;
import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.OutboxEventEntity;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 审计/outbox 领域对象与 MyBatis-Plus Entity 的显式映射。 */
public final class MybatisAuditOutboxMappings {

    private MybatisAuditOutboxMappings() {
    }

    /** 将审计事件 Entity 映射为领域对象并触发完整性校验。 */
    public static AuditEvent toDomain(AuditEventEntity entity) {
        return new AuditEvent(PersistenceUuid.toDomain(entity.auditEventId), entity.taskId, entity.actorUserId,
                entity.eventType, entity.targetType, entity.targetId, json(entity.eventPayload, "audit-payload-v1"),
                entity.payloadDigest, entity.occurredAt, entity.prevEventDigest, entity.eventDigest,
                entity.canonicalizationVersion, entity.createdBy, entity.createdAt, entity.updatedBy, entity.updatedAt);
    }

    /** 将审计事件领域对象映射为 Entity。 */
    public static AuditEventEntity toEntity(AuditEvent event) {
        AuditEventEntity entity = new AuditEventEntity();
        entity.auditEventId = PersistenceUuid.toStorage(event.auditEventId());
        entity.taskId = event.taskId();
        entity.actorUserId = event.actorUserId();
        entity.eventType = event.eventType();
        entity.targetType = event.targetType();
        entity.targetId = event.targetId();
        entity.eventPayload = event.eventPayload().json();
        entity.payloadDigest = event.payloadDigest();
        entity.occurredAt = event.occurredAt();
        entity.prevEventDigest = event.prevEventDigest();
        entity.eventDigest = event.eventDigest();
        entity.canonicalizationVersion = event.canonicalizationVersion();
        entity.createdBy = event.createdBy();
        entity.createdAt = event.createdAt();
        entity.updatedBy = event.updatedBy();
        entity.updatedAt = event.updatedAt();
        return entity;
    }

    /** 将 outbox Entity 映射为领域对象。 */
    public static OutboxEvent toDomain(OutboxEventEntity entity) {
        return new OutboxEvent(PersistenceUuid.toDomain(entity.outboxId), entity.aggregateType, entity.aggregateId,
                entity.eventType, entity.idempotencyKey, json(entity.payload, "outbox-payload-v1"), entity.createdAt,
                entity.deliveredAt, entity.deliveryAttempts, entity.createdBy, entity.updatedBy, entity.updatedAt);
    }

    /** 将 outbox 领域对象映射为 Entity。 */
    public static OutboxEventEntity toEntity(OutboxEvent event) {
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.outboxId = PersistenceUuid.toStorage(event.outboxId());
        entity.aggregateType = event.aggregateType();
        entity.aggregateId = event.aggregateId();
        entity.eventType = event.eventType();
        entity.idempotencyKey = event.idempotencyKey();
        entity.payload = event.payload().json();
        entity.createdAt = event.createdAt();
        entity.deliveredAt = event.deliveredAt();
        entity.deliveryAttempts = event.deliveryAttempts();
        entity.createdBy = event.createdBy();
        entity.updatedBy = event.updatedBy();
        entity.updatedAt = event.updatedAt();
        return entity;
    }

    private static VersionedJson json(String value, String schemaVersion) {
        return new VersionedJson(schemaVersion, value);
    }
}
