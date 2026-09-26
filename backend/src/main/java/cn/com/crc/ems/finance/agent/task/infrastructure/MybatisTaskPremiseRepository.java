package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.task.application.TaskPremiseRepository;
import cn.com.crc.ems.finance.agent.task.domain.TaskPremise;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.TaskPremiseEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.TaskPremiseMapper;

/** 使用 MyBatis-Plus Mapper 访问任务前提。 */
@Repository
public class MybatisTaskPremiseRepository implements TaskPremiseRepository {

    private final TaskPremiseMapper mapper;

    public MybatisTaskPremiseRepository(TaskPremiseMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void insert(TaskPremise premise) {
        mapper.insert(MybatisTaskMappings.toEntity(premise));
    }

    @Override
    public List<TaskPremise> findByTaskId(UUID taskId) {
        return mapper.selectList(new QueryWrapper<TaskPremiseEntity>().eq("task_id", taskId)
                        .orderByAsc("provided_at").orderByAsc("premise_id"))
                .stream().map(MybatisTaskMappings::toDomain).toList();
    }
}
