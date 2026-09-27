package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.harness.application.CheckpointRepository;
import cn.com.crc.ems.finance.agent.harness.domain.Checkpoint;
import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.CheckpointEntity;
import cn.com.crc.ems.finance.agent.harness.infrastructure.mapper.CheckpointMapper;

/** 使用 MyBatis-Plus 追加并按完整业务边界查询 checkpoint。 */
@Repository
public class MybatisCheckpointRepository implements CheckpointRepository {

    private final CheckpointMapper mapper;

    public MybatisCheckpointRepository(CheckpointMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Checkpoint> findById(UUID checkpointId, UUID taskId, UUID userId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<CheckpointEntity>()
                        .eq("checkpoint_id", checkpointId).eq("task_id", taskId).eq("user_id", userId)))
                .map(MybatisHarnessMappings::toDomain);
    }

    @Override
    public List<Checkpoint> findBySessionId(UUID sessionId, UUID taskId, UUID userId) {
        return mapper.selectList(new QueryWrapper<CheckpointEntity>().eq("session_id", sessionId)
                        .eq("task_id", taskId).eq("user_id", userId).orderByDesc("created_at"))
                .stream().map(MybatisHarnessMappings::toDomain).toList();
    }

    @Override
    public void insert(Checkpoint checkpoint) {
        mapper.insert(MybatisHarnessMappings.toEntity(checkpoint));
    }
}
