package cn.com.crc.ems.finance.agent.governance.infrastructure;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.governance.application.AuditEventRepository;
import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.AuditEventEntity;
import cn.com.crc.ems.finance.agent.governance.infrastructure.mapper.AuditEventMapper;

/** 使用 MyBatis-Plus 追加和查询审计摘要链。 */
@Repository
public class MybatisAuditEventRepository implements AuditEventRepository {

    private final AuditEventMapper mapper;

    public MybatisAuditEventRepository(AuditEventMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<AuditEvent> findById(UUID auditEventId) {
        return Optional.ofNullable(mapper.selectById(auditEventId)).map(MybatisAuditOutboxMappings::toDomain);
    }

    @Override
    public Optional<AuditEvent> findLatestByTaskId(UUID taskId) {
        QueryWrapper<AuditEventEntity> query = new QueryWrapper<>();
        if (taskId == null) {
            query.isNull("task_id");
        } else {
            query.eq("task_id", taskId);
        }
        return Optional.ofNullable(mapper.selectOne(query.orderByDesc("occurred_at").orderByDesc("audit_event_id")))
                .map(MybatisAuditOutboxMappings::toDomain);
    }

    @Override
    public AuditEvent append(AuditEvent event) {
        Objects.requireNonNull(event, "event");
        Optional<AuditEvent> latest = findLatestByTaskId(event.taskId());
        String expectedPrevious = latest.map(AuditEvent::eventDigest).orElse(null);
        if (!Objects.equals(expectedPrevious, event.prevEventDigest())) {
            throw new IllegalArgumentException("audit event does not continue the current digest chain");
        }
        mapper.insert(MybatisAuditOutboxMappings.toEntity(event));
        return event;
    }
}
