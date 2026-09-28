package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.application.TaskEventRole;
import cn.com.crc.ems.finance.agent.task.application.TaskEventStreamService;
import cn.com.crc.ems.finance.agent.task.infrastructure.InMemoryTaskEventRepository;

class TaskEventStreamServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-28T00:00:00Z");

    @Test
    void replaysOrderedEventsAndFiltersViewer() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TaskEventStreamService service = new TaskEventStreamService(new InMemoryTaskEventRepository());
        service.publish(taskId, userId, "TASK_STATUS_CHANGED", "RUNNING_STEP", "started", null, NOW);
        service.publish(taskId, userId, "TASK_PUBLISHED", "PUBLISHED", "published", "packet:1", NOW);

        assertThat(service.replay(taskId, userId, TaskEventRole.OPERATOR, 0, 10)).extracting("sequence")
                .containsExactly(1L, 2L);
        assertThat(service.replay(taskId, userId, TaskEventRole.VIEWER, 0, 10)).extracting("eventType")
                .containsExactly("TASK_PUBLISHED");
    }

    @Test
    void rejectsNegativeCursorAndSensitiveContent() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TaskEventStreamService service = new TaskEventStreamService(new InMemoryTaskEventRepository());

        assertThatThrownBy(() -> service.replay(taskId, userId, TaskEventRole.OPERATOR, -1, 10))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.publish(taskId, userId, "PUBLIC_REASON", null,
                "reasoning_content leaked", null, NOW)).isInstanceOf(IllegalArgumentException.class);
    }
}
