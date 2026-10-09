package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskPage;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
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
    public TaskPage findPageByInitiator(UUID initiatorUserId, int page, int pageSize, TaskStatus status, String keyword) {
        return findPage(new QueryWrapper<InvestigationTaskEntity>().eq("initiator_user_id", initiatorUserId), page,
                pageSize, status, keyword);
    }

    @Override
    public TaskPage findPage(int page, int pageSize, TaskStatus status, String keyword) {
        return findPage(new QueryWrapper<>(), page, pageSize, status, keyword);
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

    private TaskPage findPage(QueryWrapper<InvestigationTaskEntity> query, int page, int pageSize, TaskStatus status,
            String keyword) {
        if (status != null) query.eq("status", status.name());
        if (keyword != null) query.like("title", keyword);
        query.orderByDesc("created_at").orderByDesc("task_id");
        Page<InvestigationTaskEntity> result = mapper.selectPage(new Page<>(page, pageSize), query);
        return new TaskPage(result.getRecords().stream().map(MybatisTaskMappings::toDomain).toList(), result.getTotal(),
                page, pageSize);
    }
}
