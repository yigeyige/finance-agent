package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.governance.application.AuditEventRepository;
import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateEvent;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.MybatisTaskStateEventRepository;

/** 验证状态事件通过现有审计摘要链落库。 */
class MybatisTaskStateEventRepositoryTest {

    @Test
    void appendsStateEventAsAuditRecord() {
        AuditEventRepository audit = mock(AuditEventRepository.class);
        when(audit.findLatestByTaskId(any())).thenReturn(Optional.empty());
        when(audit.append(any())).thenAnswer(invocation -> invocation.getArgument(0));
        UUID taskId = UUID.randomUUID();
        TaskStateEvent event = new TaskStateEvent(UUID.randomUUID(), taskId, TaskStatus.DRAFT,
                TaskStatus.WAITING_SCOPE_CONFIRMATION, "submit-scope", UUID.randomUUID(),
                Instant.parse("2026-09-27T01:00:00Z"), 1);

        assertThat(new MybatisTaskStateEventRepository(audit).append(event)).isEqualTo(event);
        var captured = org.mockito.ArgumentCaptor.forClass(AuditEvent.class);
        verify(audit).append(captured.capture());
        assertThat(captured.getValue().eventType()).isEqualTo("TASK_STATE_CHANGED");
        assertThat(captured.getValue().targetId()).isEqualTo(taskId.toString());
        assertThat(captured.getValue().eventPayload().json()).contains("WAITING_SCOPE_CONFIRMATION", "submit-scope");
        assertThat(captured.getValue().hasValidIntegrity()).isTrue();
    }
}
