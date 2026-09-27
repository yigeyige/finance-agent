package cn.com.crc.ems.finance.agent.governance.infrastructure;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.governance.application.AuditEventRepository;
import cn.com.crc.ems.finance.agent.governance.application.AuditOutboxTransaction;
import cn.com.crc.ems.finance.agent.governance.application.OutboxEventRepository;
import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.governance.domain.OutboxEvent;

/** 在同一 PostgreSQL 事务中追加审计和 outbox 事件。 */
@Service
public class TransactionalAuditOutboxWriter implements AuditOutboxTransaction {

    private final AuditEventRepository auditEventRepository;
    private final OutboxEventRepository outboxEventRepository;

    public TransactionalAuditOutboxWriter(AuditEventRepository auditEventRepository,
            OutboxEventRepository outboxEventRepository) {
        this.auditEventRepository = auditEventRepository;
        this.outboxEventRepository = outboxEventRepository;
    }

    /** 任一仓储写入抛出异常时回滚本地事务。 */
    @Override
    @Transactional
    public void append(AuditEvent auditEvent, OutboxEvent outboxEvent) {
        auditEventRepository.append(auditEvent);
        outboxEventRepository.insertIfAbsent(outboxEvent);
    }
}
