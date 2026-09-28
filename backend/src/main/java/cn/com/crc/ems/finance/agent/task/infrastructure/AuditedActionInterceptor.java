package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.shared.application.DomainErrorCode;
import cn.com.crc.ems.finance.agent.shared.application.DomainException;
import cn.com.crc.ems.finance.agent.task.application.AuditedActionCommand;

/** 关键动作的失败审计拦截器；审计写失败时 fail closed。 */
@Component
public class AuditedActionInterceptor {

    private final TaskAuditEventAppender auditEventAppender;

    public AuditedActionInterceptor(TaskAuditEventAppender auditEventAppender) {
        this.auditEventAppender = auditEventAppender;
    }

    /** 执行动作并追加成功/失败审计，任一审计失败都阻止调用方假报完成。 */
    @Transactional
    public <T> T execute(AuditedActionCommand command, Supplier<T> action) {
        if (command == null || action == null) {
            throw new IllegalArgumentException("command and action are required");
        }
        try {
            T result = action.get();
            append(command, "SUCCEEDED", null);
            return result;
        } catch (RuntimeException original) {
            try {
                append(command, "FAILED", DomainErrorCode.STATE_CONFLICT.name());
            } catch (RuntimeException auditFailure) {
                throw new DomainException(DomainErrorCode.AUDIT_WRITE_FAILED);
            }
            throw original;
        }
    }

    private void append(AuditedActionCommand command, String outcome, String errorCode) {
        auditEventAppender.append(UUID.randomUUID(), command.taskId(), command.actorUserId(), command.eventType(),
                command.targetType(), command.targetId(), command.occurredAt(), payload -> {
                    payload.put("outcome", outcome);
                    if (errorCode != null) {
                        payload.put("errorCode", errorCode);
                    }
                });
    }
}
