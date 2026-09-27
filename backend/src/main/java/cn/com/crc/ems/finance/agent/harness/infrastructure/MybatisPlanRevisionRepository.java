package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.harness.application.PlanRevisionRepository;
import cn.com.crc.ems.finance.agent.harness.domain.PlanRevision;
import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.PlanRevisionEntity;
import cn.com.crc.ems.finance.agent.harness.infrastructure.mapper.PlanRevisionMapper;

/** 使用 MyBatis-Plus 追加并隔离 Harness 计划修订。 */
@Repository
public class MybatisPlanRevisionRepository implements PlanRevisionRepository {

    private final PlanRevisionMapper mapper;

    public MybatisPlanRevisionRepository(PlanRevisionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<PlanRevision> findBySessionId(UUID sessionId, UUID taskId, UUID userId) {
        return mapper.selectList(new QueryWrapper<PlanRevisionEntity>().eq("session_id", sessionId)
                        .eq("task_id", taskId).eq("user_id", userId).orderByAsc("revision_no"))
                .stream().map(MybatisHarnessMappings::toDomain).toList();
    }

    @Override
    public void insert(PlanRevision planRevision) {
        mapper.insert(MybatisHarnessMappings.toEntity(planRevision));
    }
}
