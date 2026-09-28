package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

/** 统一发布、权限过滤和游标重放的任务事件服务。 */
@Service
public class TaskEventStreamService {

    private static final Set<String> VIEWER_EVENTS = Set.of("TASK_PUBLISHED", "RESULT_REFERENCE", "PUBLIC_REASON");
    private final TaskEventRepository repository;

    public TaskEventStreamService(TaskEventRepository repository) {
        this.repository = repository;
    }

    /** 发布已经脱敏的公开事件。 */
    public PublicTaskEvent publish(UUID taskId, UUID userId, String eventType, String status, String publicReason,
            String resultReference, Instant occurredAt) {
        return repository.append(taskId, userId, eventType, status, publicReason, resultReference, occurredAt);
    }

    /** 按角色和游标读取事件，VIEWER 只可看到已允许的公开事件。 */
    public List<PublicTaskEvent> replay(UUID taskId, UUID userId, TaskEventRole role, long afterSequence, int limit) {
        if (role == null || afterSequence < 0) {
            throw new IllegalArgumentException("role and non-negative cursor are required");
        }
        List<PublicTaskEvent> events = repository.findAfter(taskId, userId, afterSequence, limit);
        return role == TaskEventRole.OPERATOR ? events
                : events.stream().filter(event -> VIEWER_EVENTS.contains(event.eventType())).toList();
    }
}
