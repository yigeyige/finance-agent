package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;
import cn.com.crc.ems.finance.agent.task.application.TaskBusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.TaskBusinessSceneSkillEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.TaskBusinessSceneSkillMapper;

/**
 *
 * @Description: 使用 MyBatis-Plus 维护任务创建时固化的业务场景选择。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisTaskBusinessSceneSkillRepository implements TaskBusinessSceneSkillRepository {
    private final TaskBusinessSceneSkillMapper mapper;

    public MybatisTaskBusinessSceneSkillRepository(TaskBusinessSceneSkillMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<UUID> findSkillIdByTaskId(UUID taskId) {
        return Optional.ofNullable(mapper.selectById(PersistenceUuid.toStorage(taskId)))
                .map(entity -> PersistenceUuid.toDomain(entity.skillId));
    }

    @Override
    public void insert(UUID taskId, UUID skillId, UUID operatorId, Instant createdAt) {
        TaskBusinessSceneSkillEntity entity = new TaskBusinessSceneSkillEntity();
        entity.taskId = PersistenceUuid.toStorage(taskId);
        entity.skillId = PersistenceUuid.toStorage(skillId);
        entity.createdBy = operatorId;
        entity.createdAt = createdAt;
        mapper.insert(entity);
    }

    @Override
    public void deleteByTaskId(UUID taskId) {
        mapper.delete(new QueryWrapper<TaskBusinessSceneSkillEntity>().eq("task_id", PersistenceUuid.toStorage(taskId)));
    }
}
