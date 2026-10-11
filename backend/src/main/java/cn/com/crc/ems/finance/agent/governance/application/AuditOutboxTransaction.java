package cn.com.crc.ems.finance.agent.governance.application;

import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.governance.domain.OutboxEvent;

/**
 *
 * @Description: 在一个本地事务中追加审计和 outbox 事件的应用端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface AuditOutboxTransaction {

    /** 审计和 outbox 任一写入失败时整体回滚。 */
    void append(AuditEvent auditEvent, OutboxEvent outboxEvent);
}
