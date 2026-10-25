package cn.com.crc.ems.finance.agent.businesspacket.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.businesspacket.application.BusinessEvidenceAccumRepository;
import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessEvidenceAccum;
import cn.com.crc.ems.finance.agent.businesspacket.infrastructure.entity.BusinessEvidenceAccumEntity;
import cn.com.crc.ems.finance.agent.businesspacket.infrastructure.mapper.BusinessEvidenceAccumMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/**
 *
 * @Description: 使用 MyBatis-Plus Mapper 读写业务处理证据工作区。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
@Repository
public class MybatisBusinessEvidenceAccumRepository implements BusinessEvidenceAccumRepository {

    private final BusinessEvidenceAccumMapper mapper;

    public MybatisBusinessEvidenceAccumRepository(BusinessEvidenceAccumMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public int findMaxRoundNo(UUID taskId) {
        return mapper.selectList(new QueryWrapper<BusinessEvidenceAccumEntity>()
                .eq("task_id", taskId).orderByDesc("round_no").last("LIMIT 1")).stream().findFirst()
                .map(item -> item.roundNo).orElse(0);
    }

    @Override
    public void save(BusinessEvidenceAccum accum) {
        BusinessEvidenceAccumEntity entity = new BusinessEvidenceAccumEntity();
        entity.evidenceAccumId = PersistenceUuid.toStorage(accum.evidenceAccumId());
        entity.taskId = accum.taskId();
        entity.roundNo = accum.roundNo();
        entity.evidence = accum.evidenceJson();
        entity.createdAt = accum.createdAt();
        mapper.insert(entity);
    }

    @Override
    public List<BusinessEvidenceAccum> findByTaskIdOrderByRound(UUID taskId) {
        return mapper.selectList(new QueryWrapper<BusinessEvidenceAccumEntity>()
                .eq("task_id", taskId).orderByAsc("round_no")).stream().map(this::toDomain).toList();
    }

    private BusinessEvidenceAccum toDomain(BusinessEvidenceAccumEntity entity) {
        return new BusinessEvidenceAccum(PersistenceUuid.toDomain(entity.evidenceAccumId), entity.taskId,
                entity.roundNo, entity.evidence, entity.createdAt);
    }
}
