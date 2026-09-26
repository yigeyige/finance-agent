package cn.com.crc.ems.finance.agent.evidence.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.evidence.application.ProfessionalConfirmationRepository;
import cn.com.crc.ems.finance.agent.evidence.domain.ProfessionalConfirmation;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.ProfessionalConfirmationEntity;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.mapper.ProfessionalConfirmationMapper;

/** 使用 MyBatis-Plus Mapper 访问专业确认记录。 */
@Repository
public class MybatisProfessionalConfirmationRepository implements ProfessionalConfirmationRepository {

    private final ProfessionalConfirmationMapper mapper;

    public MybatisProfessionalConfirmationRepository(ProfessionalConfirmationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<ProfessionalConfirmation> findByFindingId(UUID findingId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<ProfessionalConfirmationEntity>()
                        .eq("finding_id", findingId))).map(MybatisEvidenceMappings::toDomain);
    }

    @Override
    public void insert(ProfessionalConfirmation confirmation) {
        mapper.insert(MybatisEvidenceMappings.toEntity(confirmation));
    }
}
