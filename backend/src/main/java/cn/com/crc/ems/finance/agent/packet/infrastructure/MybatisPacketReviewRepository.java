package cn.com.crc.ems.finance.agent.packet.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.packet.application.PacketReviewRepository;
import cn.com.crc.ems.finance.agent.packet.domain.PacketReview;
import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.PacketReviewEntity;
import cn.com.crc.ems.finance.agent.packet.infrastructure.mapper.PacketReviewMapper;

/** 使用 MyBatis-Plus 追加和查询决策包复核。 */
@Repository
public class MybatisPacketReviewRepository implements PacketReviewRepository {

    private final PacketReviewMapper mapper;

    public MybatisPacketReviewRepository(PacketReviewMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<PacketReview> findById(UUID reviewId) {
        return Optional.ofNullable(mapper.selectById(
                cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(reviewId)))
                .map(MybatisPacketMappings::toDomain);
    }

    @Override
    public Optional<PacketReview> findByDraftId(UUID draftId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<PacketReviewEntity>().eq("draft_id", draftId)
                        .orderByDesc("reviewed_at").last("LIMIT 1")))
                .map(MybatisPacketMappings::toDomain);
    }

    @Override
    public void insert(PacketReview review) {
        mapper.insert(MybatisPacketMappings.toEntity(review));
    }
}
