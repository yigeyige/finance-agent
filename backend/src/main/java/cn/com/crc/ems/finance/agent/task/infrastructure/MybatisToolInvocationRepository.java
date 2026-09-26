package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import cn.com.crc.ems.finance.agent.task.application.ToolInvocationRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.ToolInvocationEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.ToolInvocationMapper;

/** 使用 MyBatis-Plus Mapper 保存可恢复工具调用。 */
@Repository
public class MybatisToolInvocationRepository implements ToolInvocationRepository {

    private final ToolInvocationMapper mapper;

    public MybatisToolInvocationRepository(ToolInvocationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<ToolInvocation> findByIdempotencyKey(String idempotencyKey) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<ToolInvocationEntity>()
                        .eq("idempotency_key", idempotencyKey))).map(MybatisTaskMappings::toDomain);
    }

    @Override
    public void insert(ToolInvocation invocation) {
        mapper.insert(MybatisTaskMappings.toEntity(invocation));
    }

    @Override
    public boolean updateStatus(String idempotencyKey, InvocationStatus status, String responseDigest,
            String errorCode, Instant completedAt, Instant updatedAt, UUID actorId) {
        return mapper.update(null, new UpdateWrapper<ToolInvocationEntity>().eq("idempotency_key", idempotencyKey)
                .set("status", status.name()).set("response_digest", responseDigest).set("error_code", errorCode)
                .set("completed_at", completedAt).set("updated_at", updatedAt).set("updated_by", actorId)) == 1;
    }
}
