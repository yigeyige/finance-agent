package cn.com.crc.ems.finance.agent.evidence.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.evidence.application.FindingEvidenceRepository;
import cn.com.crc.ems.finance.agent.evidence.domain.FindingEvidence;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.FindingEvidenceEntity;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.mapper.FindingEvidenceMapper;

/**
 *
 * @Description: 使用 MyBatis-Plus Mapper 访问判断证据引用。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisFindingEvidenceRepository implements FindingEvidenceRepository {

    private final FindingEvidenceMapper mapper;

    public MybatisFindingEvidenceRepository(FindingEvidenceMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<FindingEvidence> findByFindingId(UUID findingId) {
        return mapper.selectList(new QueryWrapper<FindingEvidenceEntity>().eq("finding_id", findingId)
                        .orderByAsc("created_at")).stream().map(MybatisEvidenceMappings::toDomain).toList();
    }

    @Override
    public void insert(FindingEvidence evidence) {
        mapper.insert(MybatisEvidenceMappings.toEntity(evidence));
    }
}
