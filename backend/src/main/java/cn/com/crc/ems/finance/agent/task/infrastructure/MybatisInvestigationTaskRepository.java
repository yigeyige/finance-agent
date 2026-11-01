package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskPage;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.InvestigationTaskEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.InvestigationTaskMapper;

/**
 *
 * @Description: 使用 MyBatis-Plus Mapper 访问调查任务，以绑定查询执行列表分页及乐观版本更新。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-01 00:00:00
 */
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
        return findPage(initiatorUserId, page, pageSize, status, keyword);
    }

    @Override
    public TaskPage findPage(int page, int pageSize, TaskStatus status, String keyword) {
        return findPage(null, page, pageSize, status, keyword);
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

    /**
     * @description: 在数据库端执行同条件计数与有界查询，不依赖未配置的分页拦截器。
     * @param: initiatorUserId 普通操作人的身份范围，管理员查询时为空
     * @param: page 从一开始的页码
     * @param: pageSize 每页条数，不超过一百
     * @param: status 可选任务状态
     * @param: keyword 可选标题关键字
     * @return: 含真实总数和当前页记录的结果
     * @author: yigeyige99
     */
    private TaskPage findPage(UUID initiatorUserId, int page, int pageSize, TaskStatus status,
            String keyword) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("任务分页参数不合法");
        }
        String statusName = status == null ? null : status.name();
        long total = mapper.countPageRows(initiatorUserId, statusName, keyword);
        long offset = ((long) page - 1) * pageSize;
        List<InvestigationTaskEntity> records = total == 0 ? List.of()
                : mapper.selectPageRows(initiatorUserId, statusName, keyword, pageSize, offset);
        return new TaskPage(records.stream().map(MybatisTaskMappings::toDomain).toList(), total, page, pageSize);
    }
}
