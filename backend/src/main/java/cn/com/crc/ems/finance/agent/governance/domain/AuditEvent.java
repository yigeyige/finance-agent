package cn.com.crc.ems.finance.agent.governance.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/**
 *
 * @Description: 只追加的任务审计事件，携带规范化 payload 和前序摘要链。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record AuditEvent(UUID auditEventId, UUID taskId, UUID actorUserId, String eventType, String targetType,
    String targetId, VersionedJson eventPayload, String payloadDigest, Instant occurredAt,
        String prevEventDigest, String eventDigest, String canonicalizationVersion, UUID createdBy,
        Instant createdAt, UUID updatedBy, Instant updatedAt) {

    public AuditEvent {
        occurredAt = postgresTimestamp(occurredAt);
        requireId(auditEventId, "auditEventId");
        requireText(eventType, "eventType");
        requireText(targetType, "targetType");
        requireText(targetId, "targetId");
        requireText(canonicalizationVersion, "canonicalizationVersion");
        if (occurredAt == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("audit timestamps are required");
        }
        eventPayload = AuditDigest.canonicalize(eventPayload);
        requireDigest(payloadDigest, "payloadDigest");
        requireDigest(eventDigest, "eventDigest");
        if (prevEventDigest != null) {
            requireDigest(prevEventDigest, "prevEventDigest");
        }
        if (!payloadDigest.equals(AuditDigest.payloadDigest(eventPayload))) {
            throw new IllegalArgumentException("payloadDigest does not match eventPayload");
        }
        String expected = AuditDigest.eventDigest(taskId, actorUserId, eventType, targetType, targetId,
                canonicalizationVersion, payloadDigest, occurredAt.toString(), prevEventDigest);
        if (!eventDigest.equals(expected)) {
            throw new IllegalArgumentException("eventDigest does not match audit event");
        }
    }

    /** 创建带完整 payload 摘要和事件摘要的审计事件。 */
    public static AuditEvent create(UUID auditEventId, UUID taskId, UUID actorUserId, String eventType,
            String targetType, String targetId, VersionedJson payload, Instant occurredAt,
            String prevEventDigest, UUID createdBy, Instant createdAt, UUID updatedBy, Instant updatedAt) {
        occurredAt = postgresTimestamp(occurredAt);
        VersionedJson canonical = AuditDigest.canonicalize(payload);
        String payloadDigest = AuditDigest.payloadDigest(canonical);
        String eventDigest = AuditDigest.eventDigest(taskId, actorUserId, eventType, targetType, targetId,
                AuditDigest.VERSION, payloadDigest, occurredAt.toString(), prevEventDigest);
        return new AuditEvent(auditEventId, taskId, actorUserId, eventType, targetType, targetId, canonical,
                payloadDigest, occurredAt, prevEventDigest, eventDigest, AuditDigest.VERSION, createdBy,
                createdAt, updatedBy, updatedAt);
    }

    /** 重新校验事件摘要链所需的当前事件完整性。 */
    public boolean hasValidIntegrity() {
        return payloadDigest.equals(AuditDigest.payloadDigest(eventPayload))
                && eventDigest.equals(AuditDigest.eventDigest(taskId, actorUserId, eventType, targetType, targetId,
                        canonicalizationVersion, payloadDigest, occurredAt.toString(), prevEventDigest));
    }

    private static void requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static void requireDigest(String value, String name) {
        if (value == null || !value.matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException(name + " must be a 64-character hexadecimal digest");
        }
    }

    /**
     *
     * @description: 将参与审计摘要的事件时间规整为 PostgreSQL timestamptz 可持久化的微秒精度，确保重读后摘要链仍可复算。
     *
     * @param: value 原始事件时间。
     * @return: 适合 PostgreSQL 持久化与摘要计算的事件时间。
     * @author: yigeyige99
     */
    private static Instant postgresTimestamp(Instant value) {
        if (value == null) {
            throw new IllegalArgumentException("occurredAt is required");
        }
        return value.truncatedTo(ChronoUnit.MICROS);
    }
}
