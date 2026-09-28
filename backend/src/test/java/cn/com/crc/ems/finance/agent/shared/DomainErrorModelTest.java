package cn.com.crc.ems.finance.agent.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.shared.application.DomainErrorCode;
import cn.com.crc.ems.finance.agent.shared.application.DomainException;
import cn.com.crc.ems.finance.agent.task.application.AuditedActionCommand;
import cn.com.crc.ems.finance.agent.task.infrastructure.AuditedActionInterceptor;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;

class DomainErrorModelTest {

    @Test
    void errorCatalogDoesNotExposeInfrastructureDetails() {
        assertThat(DomainErrorCode.INTERNAL_ERROR.safeMessage()).doesNotContain("SQL", "path", "token");
        assertThat(DomainErrorCode.AUDIT_WRITE_FAILED.retryClass().name()).isEqualTo("MANUAL_RECONCILIATION");
    }

    @Test
    void auditWriteFailureFailsClosed() {
        TaskAuditEventAppender audit = mock(TaskAuditEventAppender.class);
        doThrow(new IllegalStateException("database password leaked")).when(audit).append(any(), any(), any(), any(),
                any(), any(), any(), any());
        AuditedActionInterceptor interceptor = new AuditedActionInterceptor(audit);

        assertThatThrownBy(() -> interceptor.execute(new AuditedActionCommand(UUID.randomUUID(), UUID.randomUUID(),
                "TASK_ACTION", "task", "1", Instant.parse("2026-09-28T00:00:00Z")), () -> {
                    throw new IllegalStateException("sql /secret/password");
                })).isInstanceOf(DomainException.class).extracting("errorCode")
                .isEqualTo(DomainErrorCode.AUDIT_WRITE_FAILED);
    }
}
