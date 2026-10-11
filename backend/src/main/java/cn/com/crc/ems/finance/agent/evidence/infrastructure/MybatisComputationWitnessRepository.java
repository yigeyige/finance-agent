package cn.com.crc.ems.finance.agent.evidence.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.evidence.application.ComputationWitnessRepository;
import cn.com.crc.ems.finance.agent.evidence.domain.ComputationWitness;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.ComputationWitnessEntity;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.mapper.ComputationWitnessMapper;

/**
 *
 * @Description: 使用 MyBatis-Plus Mapper 访问计算见证。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisComputationWitnessRepository implements ComputationWitnessRepository {

    private final ComputationWitnessMapper mapper;

    public MybatisComputationWitnessRepository(ComputationWitnessMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<ComputationWitness> findByFindingId(UUID findingId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<ComputationWitnessEntity>()
                        .eq("finding_id", findingId))).map(MybatisEvidenceMappings::toDomain);
    }

    @Override
    public void insert(ComputationWitness witness) {
        mapper.insert(MybatisEvidenceMappings.toEntity(witness));
    }
}
