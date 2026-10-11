package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.task.application.ScopeRevisionRepository;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.ScopeRevisionEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.ScopeRevisionMapper;

/**
 *
 * @Description: 使用 MyBatis-Plus Mapper 追加和查询不可变范围修订。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisScopeRevisionRepository implements ScopeRevisionRepository {

    private final ScopeRevisionMapper mapper;

    public MybatisScopeRevisionRepository(ScopeRevisionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<ScopeRevision> findById(UUID scopeRevisionId) {
        return Optional.ofNullable(mapper.selectById(
                cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(scopeRevisionId)))
                .map(MybatisTaskMappings::toDomain);
    }

    @Override
    public Optional<ScopeRevision> findLatestByTaskId(UUID taskId) {
        return mapper.selectList(new QueryWrapper<ScopeRevisionEntity>().eq("task_id", taskId)
                        .orderByDesc("revision_no").last("LIMIT 1")).stream().findFirst().map(MybatisTaskMappings::toDomain);
    }

    @Override
    public void insert(ScopeRevision revision) {
        mapper.insert(MybatisTaskMappings.toEntity(revision));
    }
}
