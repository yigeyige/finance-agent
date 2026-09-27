package cn.com.crc.ems.finance.agent.packet.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.packet.application.PacketRevisionRepository;
import cn.com.crc.ems.finance.agent.packet.domain.PacketRevision;
import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.PacketRevisionEntity;
import cn.com.crc.ems.finance.agent.packet.infrastructure.mapper.PacketRevisionMapper;

/** 使用 MyBatis-Plus 只追加和只读查询已发布决策包修订。 */
@Repository
public class MybatisPacketRevisionRepository implements PacketRevisionRepository {

    private final PacketRevisionMapper mapper;

    public MybatisPacketRevisionRepository(PacketRevisionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<PacketRevision> findById(UUID packetRevisionId) {
        return Optional.ofNullable(mapper.selectById(packetRevisionId)).map(MybatisPacketMappings::toDomain);
    }

    @Override
    public Optional<PacketRevision> findPublishedByTaskId(UUID taskId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<PacketRevisionEntity>().eq("task_id", taskId)
                        .isNotNull("published_at").orderByDesc("revision_no").last("LIMIT 1")))
                .map(MybatisPacketMappings::toDomain);
    }

    @Override
    public void insert(PacketRevision revision) {
        mapper.insert(MybatisPacketMappings.toEntity(revision));
    }
}
