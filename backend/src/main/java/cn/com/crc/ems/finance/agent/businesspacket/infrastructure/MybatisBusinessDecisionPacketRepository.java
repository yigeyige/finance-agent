package cn.com.crc.ems.finance.agent.businesspacket.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.businesspacket.application.BusinessDecisionPacketRepository;
import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessDecisionPacket;
import cn.com.crc.ems.finance.agent.businesspacket.infrastructure.entity.BusinessDecisionPacketEntity;
import cn.com.crc.ems.finance.agent.businesspacket.infrastructure.mapper.BusinessDecisionPacketMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/**
 *
 * @Description: 使用 MyBatis-Plus Mapper 读写业务决策包。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
@Repository
public class MybatisBusinessDecisionPacketRepository implements BusinessDecisionPacketRepository {

    private final BusinessDecisionPacketMapper mapper;

    public MybatisBusinessDecisionPacketRepository(BusinessDecisionPacketMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<BusinessDecisionPacket> findLatestByTaskId(UUID taskId) {
        return mapper.selectList(new QueryWrapper<BusinessDecisionPacketEntity>()
                .eq("task_id", taskId).orderByDesc("revision").last("LIMIT 1")).stream().findFirst()
                .map(this::toDomain);
    }

    @Override
    public Optional<BusinessDecisionPacket> findByTaskIdAndRevision(UUID taskId, int revision) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<BusinessDecisionPacketEntity>()
                .eq("task_id", taskId).eq("revision", revision))).map(this::toDomain);
    }

    @Override
    public void insert(BusinessDecisionPacket packet) {
        BusinessDecisionPacketEntity entity = new BusinessDecisionPacketEntity();
        entity.packetId = PersistenceUuid.toStorage(packet.packetId());
        entity.taskId = packet.taskId();
        entity.revision = packet.revision();
        entity.canonicalContent = packet.canonicalContent();
        entity.contentDigest = packet.contentDigest();
        entity.createdAt = packet.createdAt();
        mapper.insert(entity);
    }

    private BusinessDecisionPacket toDomain(BusinessDecisionPacketEntity entity) {
        return new BusinessDecisionPacket(PersistenceUuid.toDomain(entity.packetId), entity.taskId,
                entity.revision, entity.canonicalContent, entity.contentDigest, entity.createdAt);
    }
}
