package cn.com.crc.ems.finance.agent.packet.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.packet.application.HandoffRepository;
import cn.com.crc.ems.finance.agent.packet.domain.Handoff;
import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.HandoffEntity;
import cn.com.crc.ems.finance.agent.packet.infrastructure.mapper.HandoffMapper;

/**
 *
 * @Description: 使用 MyBatis-Plus 追加和查询交接记录。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisHandoffRepository implements HandoffRepository {

    private final HandoffMapper mapper;

    public MybatisHandoffRepository(HandoffMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Handoff> findById(UUID handoffId) {
        return Optional.ofNullable(mapper.selectById(
                cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(handoffId)))
                .map(MybatisPacketMappings::toDomain);
    }

    @Override
    public Optional<Handoff> findByPacketRevisionId(UUID packetRevisionId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<HandoffEntity>().eq("packet_revision_id", packetRevisionId)
                        .orderByDesc("handed_off_at").last("LIMIT 1")))
                .map(MybatisPacketMappings::toDomain);
    }

    @Override
    public void insert(Handoff handoff) {
        mapper.insert(MybatisPacketMappings.toEntity(handoff));
    }
}
