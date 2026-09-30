package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.task.application.InvestigationStepRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.InvestigationStepEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.InvestigationStepMapper;

/** 使用 MyBatis-Plus Mapper 访问有序调查步骤。 */
@Repository
public class MybatisInvestigationStepRepository implements InvestigationStepRepository {

    private final InvestigationStepMapper mapper;

    public MybatisInvestigationStepRepository(InvestigationStepMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<InvestigationStep> findById(UUID stepId) {
        return Optional.ofNullable(mapper.selectById(
                cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(stepId)))
                .map(MybatisTaskMappings::toDomain);
    }

    @Override
    public List<InvestigationStep> findByTaskId(UUID taskId) {
        return mapper.selectList(new QueryWrapper<InvestigationStepEntity>().eq("task_id", taskId)
                        .orderByAsc("sequence_no"))
                .stream().map(MybatisTaskMappings::toDomain).toList();
    }

    @Override
    public Optional<InvestigationStep> findNextForUpdateSkipLocked(String taskStatus, String stepStatus) {
        if (taskStatus == null || taskStatus.isBlank() || stepStatus == null || stepStatus.isBlank()) {
            throw new IllegalArgumentException("taskStatus and stepStatus are required");
        }
        return Optional.ofNullable(mapper.selectNextForUpdateSkipLocked(taskStatus, stepStatus))
                .map(MybatisTaskMappings::toDomain);
    }

    @Override
    public void insert(InvestigationStep step) {
        mapper.insert(MybatisTaskMappings.toEntity(step));
    }

    @Override
    public int updateStatus(UUID stepId, cn.com.crc.ems.finance.agent.task.domain.StepStatus status,
            UUID actorUserId, java.time.Instant updatedAt) {
        return mapper.updateStatus(stepId, status.name(), actorUserId, updatedAt);
    }
}
