package cn.com.crc.ems.finance.agent.task.infrastructure;

import org.springframework.stereotype.Repository;
import cn.com.crc.ems.finance.agent.task.application.TaskStateEventRepository;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateEvent;

/** 将任务状态事件转换为治理审计事件，复用既有摘要链和 MyBatis-Plus 仓储。 */
@Repository
public class MybatisTaskStateEventRepository implements TaskStateEventRepository {

    private static final String EVENT_TYPE = "TASK_STATE_CHANGED";

    private final TaskAuditEventAppender auditEventAppender;

    public MybatisTaskStateEventRepository(TaskAuditEventAppender auditEventAppender) {
        this.auditEventAppender = auditEventAppender;
    }

    @Override
    public TaskStateEvent append(TaskStateEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event is required");
        }
        auditEventAppender.append(event.eventId(), event.taskId(), event.actorUserId(), EVENT_TYPE, "task",
                event.taskId().toString(), event.occurredAt(), payload -> {
                    payload.put("fromStatus", event.fromStatus().name());
                    payload.put("toStatus", event.toStatus().name());
                    payload.put("reason", event.reason());
                    payload.put("rowVersion", event.rowVersion());
                });
        return event;
    }
}
