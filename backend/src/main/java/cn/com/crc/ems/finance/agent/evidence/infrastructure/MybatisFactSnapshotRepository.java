package cn.com.crc.ems.finance.agent.evidence.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.evidence.application.FactSnapshotRepository;
import cn.com.crc.ems.finance.agent.evidence.domain.FactSnapshot;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.FactSnapshotEntity;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.mapper.FactSnapshotMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/**
 *
 * @Description: 使用 MyBatis-Plus Mapper 访问事实快照。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisFactSnapshotRepository implements FactSnapshotRepository {

    private final FactSnapshotMapper mapper;

    public MybatisFactSnapshotRepository(FactSnapshotMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<FactSnapshot> findById(UUID snapshotId) {
        return Optional.ofNullable(mapper.selectById(PersistenceUuid.toStorage(snapshotId)))
                .map(MybatisEvidenceMappings::toDomain);
    }

    @Override
    public List<FactSnapshot> findByTaskId(UUID taskId) {
        return mapper.selectList(new QueryWrapper<FactSnapshotEntity>().eq("task_id", taskId)
                        .orderByDesc("checked_at")).stream().map(MybatisEvidenceMappings::toDomain).toList();
    }

    @Override
    public void insert(FactSnapshot snapshot) {
        mapper.insert(MybatisEvidenceMappings.toEntity(snapshot));
    }
}
