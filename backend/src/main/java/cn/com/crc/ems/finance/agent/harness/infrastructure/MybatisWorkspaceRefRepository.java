package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.harness.application.WorkspaceRefRepository;
import cn.com.crc.ems.finance.agent.harness.domain.WorkspaceRef;
import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.WorkspaceRefEntity;
import cn.com.crc.ems.finance.agent.harness.infrastructure.mapper.WorkspaceRefMapper;

/** 使用 MyBatis-Plus 追加并隔离 workspace 引用。 */
@Repository
public class MybatisWorkspaceRefRepository implements WorkspaceRefRepository {

    private final WorkspaceRefMapper mapper;

    public MybatisWorkspaceRefRepository(WorkspaceRefMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<WorkspaceRef> findById(UUID workspaceRefId, UUID taskId, UUID userId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<WorkspaceRefEntity>()
                        .eq("workspace_ref_id", workspaceRefId).eq("task_id", taskId).eq("user_id", userId)))
                .map(MybatisHarnessMappings::toDomain);
    }

    @Override
    public List<WorkspaceRef> findBySessionId(UUID sessionId, UUID taskId, UUID userId) {
        return mapper.selectList(new QueryWrapper<WorkspaceRefEntity>().eq("session_id", sessionId)
                        .eq("task_id", taskId).eq("user_id", userId).orderByAsc("relative_path"))
                .stream().map(MybatisHarnessMappings::toDomain).toList();
    }

    @Override
    public void insert(WorkspaceRef workspaceRef) {
        mapper.insert(MybatisHarnessMappings.toEntity(workspaceRef));
    }
}
