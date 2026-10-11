package cn.com.crc.ems.finance.agent.governance.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import cn.com.crc.ems.finance.agent.governance.application.OutboxEventRepository;
import cn.com.crc.ems.finance.agent.governance.domain.OutboxEvent;
import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.OutboxEventEntity;
import cn.com.crc.ems.finance.agent.governance.infrastructure.mapper.OutboxEventMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/**
 *
 * @Description: 使用 MyBatis-Plus 访问本地 outbox 并提供幂等和投递状态更新。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisOutboxEventRepository implements OutboxEventRepository {

    private final OutboxEventMapper mapper;

    public MybatisOutboxEventRepository(OutboxEventMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<OutboxEvent> findByIdempotencyKey(String idempotencyKey) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<OutboxEventEntity>()
                .eq("idempotency_key", idempotencyKey))).map(MybatisAuditOutboxMappings::toDomain);
    }

    @Override
    public List<OutboxEvent> findPending(int limit, int maxAttempts) {
        if (limit < 1 || limit > 1000 || maxAttempts < 1) {
            throw new IllegalArgumentException("limit must be 1..1000 and maxAttempts must be positive");
        }
        QueryWrapper<OutboxEventEntity> query = new QueryWrapper<OutboxEventEntity>()
                .isNull("delivered_at").lt("delivery_attempts", maxAttempts)
                .orderByAsc("created_at").orderByAsc("outbox_id").last("LIMIT " + limit);
        return mapper.selectList(query).stream().map(MybatisAuditOutboxMappings::toDomain).toList();
    }

    @Override
    public OutboxEvent insertIfAbsent(OutboxEvent event) {
        Optional<OutboxEvent> existing = findByIdempotencyKey(event.idempotencyKey());
        if (existing.isPresent()) {
            return existing.get();
        }
        mapper.insert(MybatisAuditOutboxMappings.toEntity(event));
        return event;
    }

    @Override
    public void recordDeliveryAttempt(UUID outboxId, Instant updatedAt) {
        if (outboxId == null || updatedAt == null) {
            throw new IllegalArgumentException("outboxId and updatedAt are required");
        }
        OutboxEventEntity update = new OutboxEventEntity();
        update.updatedAt = updatedAt;
        mapper.update(update, new UpdateWrapper<OutboxEventEntity>()
                .eq("outbox_id", PersistenceUuid.toStorage(outboxId))
                .isNull("delivered_at")
                .setSql("delivery_attempts = delivery_attempts + 1"));
    }

    @Override
    public void markDelivered(UUID outboxId, Instant deliveredAt, UUID updatedBy, Instant updatedAt) {
        if (outboxId == null || deliveredAt == null || updatedAt == null) {
            throw new IllegalArgumentException("outboxId and delivery timestamps are required");
        }
        OutboxEventEntity update = new OutboxEventEntity();
        update.deliveredAt = deliveredAt;
        update.updatedBy = updatedBy;
        update.updatedAt = updatedAt;
        mapper.update(update, new UpdateWrapper<OutboxEventEntity>()
                .eq("outbox_id", PersistenceUuid.toStorage(outboxId)).isNull("delivered_at"));
    }
}
