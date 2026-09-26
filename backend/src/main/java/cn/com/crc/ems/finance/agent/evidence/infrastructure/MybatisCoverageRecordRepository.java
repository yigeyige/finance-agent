package cn.com.crc.ems.finance.agent.evidence.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.evidence.application.CoverageRecordRepository;
import cn.com.crc.ems.finance.agent.evidence.domain.CoverageRecord;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.CoverageRecordEntity;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.mapper.CoverageRecordMapper;

/** 使用 MyBatis-Plus Mapper 访问覆盖记录。 */
@Repository
public class MybatisCoverageRecordRepository implements CoverageRecordRepository {

    private final CoverageRecordMapper mapper;

    public MybatisCoverageRecordRepository(CoverageRecordMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<CoverageRecord> findByTaskId(UUID taskId) {
        return mapper.selectList(new QueryWrapper<CoverageRecordEntity>().eq("task_id", taskId)
                        .orderByDesc("checked_at")).stream().map(MybatisEvidenceMappings::toDomain).toList();
    }

    @Override
    public void insert(CoverageRecord coverage) {
        mapper.insert(MybatisEvidenceMappings.toEntity(coverage));
    }
}
