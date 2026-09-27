package cn.com.crc.ems.finance.agent.governance.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;

/** 审计事件追加与完整性查询端口。 */
public interface AuditEventRepository {

    /** 查询审计事件并在映射边界校验摘要。 */
    Optional<AuditEvent> findById(UUID auditEventId);

    /** 查询指定任务摘要链的最后事件。 */
    Optional<AuditEvent> findLatestByTaskId(UUID taskId);

    /** 追加审计事件，不提供更新或删除能力。 */
    AuditEvent append(AuditEvent event);
}
