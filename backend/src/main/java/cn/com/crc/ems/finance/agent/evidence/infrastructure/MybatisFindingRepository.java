package cn.com.crc.ems.finance.agent.evidence.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.evidence.application.FindingRepository;
import cn.com.crc.ems.finance.agent.evidence.domain.Finding;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.FindingEntity;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.mapper.FindingMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/** 使用 MyBatis-Plus Mapper 访问判断结果。 */
@Repository
public class MybatisFindingRepository implements FindingRepository {

    private final FindingMapper mapper;

    public MybatisFindingRepository(FindingMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Finding> findById(UUID findingId) {
        return Optional.ofNullable(mapper.selectById(PersistenceUuid.toStorage(findingId)))
                .map(MybatisEvidenceMappings::toDomain);
    }

    @Override
    public List<Finding> findByTaskId(UUID taskId) {
        return mapper.selectList(new QueryWrapper<FindingEntity>().eq("task_id", taskId)
                        .orderByAsc("created_at")).stream().map(MybatisEvidenceMappings::toDomain).toList();
    }

    @Override
    public void insert(Finding finding) {
        mapper.insert(MybatisEvidenceMappings.toEntity(finding));
    }
}
