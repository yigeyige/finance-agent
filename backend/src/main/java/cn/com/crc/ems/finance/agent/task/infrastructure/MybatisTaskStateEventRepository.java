package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.governance.application.AuditEventRepository;
import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.task.application.TaskStateEventRepository;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateEvent;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 将任务状态事件转换为治理审计事件，复用既有摘要链和 MyBatis-Plus 仓储。 */
@Repository
public class MybatisTaskStateEventRepository implements TaskStateEventRepository {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
    private static final String EVENT_TYPE = "TASK_STATE_CHANGED";

    private final AuditEventRepository auditEventRepository;

    public MybatisTaskStateEventRepository(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Override
    public TaskStateEvent append(TaskStateEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event is required");
        }
        Optional<AuditEvent> previous = auditEventRepository.findLatestByTaskId(event.taskId());
        auditEventRepository.append(toAuditEvent(event, previous));
        return event;
    }

    private static AuditEvent toAuditEvent(TaskStateEvent event, Optional<AuditEvent> previous) {
        try {
            ObjectNode payload = JSON_MAPPER.createObjectNode();
            payload.put("fromStatus", event.fromStatus().name());
            payload.put("toStatus", event.toStatus().name());
            payload.put("reason", event.reason());
            payload.put("rowVersion", event.rowVersion());
            return AuditEvent.create(event.eventId(), event.taskId(), event.actorUserId(), EVENT_TYPE, "task",
                    event.taskId().toString(), new VersionedJson("audit-payload-v1",
                            JSON_MAPPER.writeValueAsString(payload)), event.occurredAt(),
                    previous.map(AuditEvent::eventDigest).orElse(null), event.actorUserId(), event.occurredAt(),
                    event.actorUserId(), event.occurredAt());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("state event payload cannot be serialized", exception);
        }
    }
}
