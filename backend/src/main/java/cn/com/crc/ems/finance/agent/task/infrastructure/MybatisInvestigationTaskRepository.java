package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.InvestigationTaskEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.InvestigationTaskMapper;

/** 使用 MyBatis-Plus Mapper 访问调查任务并执行乐观版本更新。 */
@Repository
public class MybatisInvestigationTaskRepository implements InvestigationTaskRepository {

    private final InvestigationTaskMapper mapper;

    public MybatisInvestigationTaskRepository(InvestigationTaskMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<InvestigationTask> findById(UUID taskId) {
        return Optional.ofNullable(mapper.selectById(
                cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(taskId)))
                .map(MybatisTaskMappings::toDomain);
    }

    @Override
    public Optional<InvestigationTask> findByIdForUpdate(UUID taskId) {
        return Optional.ofNullable(mapper.selectByIdForUpdate(
                cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(taskId)))
                .map(MybatisTaskMappings::toDomain);
    }

    @Override
    public void insert(InvestigationTask task) {
        mapper.insert(MybatisTaskMappings.toEntity(task));
    }

    @Override
    public boolean updateWithVersion(InvestigationTask task, long expectedRowVersion) {
        InvestigationTaskEntity entity = MybatisTaskMappings.toEntity(task);
        entity.rowVersion = expectedRowVersion + 1;
        return mapper.update(entity, new UpdateWrapper<InvestigationTaskEntity>().eq("task_id", task.taskId())
                .eq("row_version", expectedRowVersion)) == 1;
    }
}
