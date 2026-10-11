package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.task.application.StepApprovalRepository;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.StepApprovalEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.StepApprovalMapper;

/**
 *
 * @Description: 使用 MyBatis-Plus Mapper 访问步骤审批历史。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisStepApprovalRepository implements StepApprovalRepository {

    private final StepApprovalMapper mapper;

    public MybatisStepApprovalRepository(StepApprovalMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<StepApproval> findByStepId(UUID stepId) {
        return mapper.selectList(new QueryWrapper<StepApprovalEntity>().eq("step_id", stepId)
                        .orderByAsc("revision_no"))
                .stream().map(MybatisTaskMappings::toDomain).toList();
    }

    @Override
    public Optional<StepApproval> findById(UUID approvalId) {
        return Optional.ofNullable(mapper.selectById(
                cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(approvalId)))
                .map(MybatisTaskMappings::toDomain);
    }

    @Override
    public void insert(StepApproval approval) {
        mapper.insert(MybatisTaskMappings.toEntity(approval));
    }

    @Override
    public int markStaleByScopeRevision(UUID taskId, UUID scopeRevisionId, UUID actorUserId,
            java.time.Instant updatedAt) {
        return mapper.markStaleByScopeRevision(cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(taskId),
                cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(scopeRevisionId),
                cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(actorUserId), updatedAt);
    }

    @Override
    public int revoke(UUID approvalId, UUID actorUserId, java.time.Instant updatedAt) {
        return mapper.revoke(cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(approvalId),
                cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(actorUserId), updatedAt);
    }
}
