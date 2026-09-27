package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.governance.application.AuditEventRepository;
import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 复用治理审计摘要链追加任务动作，payload 只允许最小结构化元数据。 */
@Component
public final class TaskAuditEventAppender {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
    private static final String PAYLOAD_SCHEMA = "audit-payload-v1";

    private final AuditEventRepository auditEventRepository;

    public TaskAuditEventAppender(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    public AuditEvent append(UUID eventId, UUID taskId, UUID actorUserId, String eventType,
            String targetType, String targetId, Instant occurredAt, Consumer<ObjectNode> payloadWriter) {
        if (eventId == null || taskId == null || eventType == null || eventType.isBlank()
                || targetType == null || targetType.isBlank() || targetId == null || targetId.isBlank()
                || occurredAt == null || payloadWriter == null) {
            throw new IllegalArgumentException("audit event identity, type, time and payload are required");
        }
        ObjectNode payload = JSON_MAPPER.createObjectNode();
        payloadWriter.accept(payload);
        try {
            Optional<AuditEvent> previous = auditEventRepository.findLatestByTaskId(taskId);
            AuditEvent event = AuditEvent.create(eventId, taskId, actorUserId, eventType, targetType, targetId,
                    new VersionedJson(PAYLOAD_SCHEMA, JSON_MAPPER.writeValueAsString(payload)), occurredAt,
                    previous.map(AuditEvent::eventDigest).orElse(null), actorUserId, occurredAt, actorUserId,
                    occurredAt);
            return auditEventRepository.append(event);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("audit payload cannot be serialized", exception);
        }
    }
}
