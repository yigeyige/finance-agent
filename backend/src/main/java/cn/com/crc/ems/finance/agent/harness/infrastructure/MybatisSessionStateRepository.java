package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.harness.application.SessionStateRepository;
import cn.com.crc.ems.finance.agent.harness.domain.SessionState;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;
import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.SessionStateEntity;
import cn.com.crc.ems.finance.agent.harness.infrastructure.mapper.SessionStateMapper;

/** 使用 MyBatis-Plus 隔离并更新 Harness session 状态。 */
@Repository
public class MybatisSessionStateRepository implements SessionStateRepository {

    private final SessionStateMapper mapper;

    public MybatisSessionStateRepository(SessionStateMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<SessionState> findById(UUID sessionId, UUID taskId, UUID userId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<SessionStateEntity>()
                        .eq("session_id", sessionId).eq("task_id", taskId).eq("user_id", userId)))
                .map(MybatisHarnessMappings::toDomain);
    }

    @Override
    public void insert(SessionState sessionState) {
        mapper.insert(MybatisHarnessMappings.toEntity(sessionState));
    }

    @Override
    public boolean updateState(UUID sessionId, UUID taskId, UUID userId, StateReference stateRef,
            UUID updatedBy, Instant updatedAt) {
        if (stateRef == null || updatedAt == null) {
            throw new IllegalArgumentException("stateRef and updatedAt are required");
        }
        SessionStateEntity entity = new SessionStateEntity();
        entity.stateSchemaVersion = stateRef.schemaVersion();
        entity.stateRef = stateRef.json();
        entity.stateDigest = stateRef.digest();
        entity.updatedBy = updatedBy;
        entity.updatedAt = updatedAt;
        return mapper.update(entity, new QueryWrapper<SessionStateEntity>().eq("session_id", sessionId)
                        .eq("task_id", taskId).eq("user_id", userId)) > 0;
    }
}
