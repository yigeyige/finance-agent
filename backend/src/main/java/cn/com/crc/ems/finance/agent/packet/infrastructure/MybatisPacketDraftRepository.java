package cn.com.crc.ems.finance.agent.packet.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.packet.application.PacketDraftRepository;
import cn.com.crc.ems.finance.agent.packet.domain.PacketDraft;
import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.PacketDraftEntity;
import cn.com.crc.ems.finance.agent.packet.infrastructure.mapper.PacketDraftMapper;

/** 使用 MyBatis-Plus 追加和查询决策包草稿。 */
@Repository
public class MybatisPacketDraftRepository implements PacketDraftRepository {

    private final PacketDraftMapper mapper;

    public MybatisPacketDraftRepository(PacketDraftMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<PacketDraft> findById(UUID draftId) {
        return Optional.ofNullable(mapper.selectById(draftId)).map(MybatisPacketMappings::toDomain);
    }

    @Override
    public Optional<PacketDraft> findCurrentByTaskId(UUID taskId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<PacketDraftEntity>().eq("task_id", taskId)
                        .eq("status", "CURRENT").orderByDesc("draft_no").last("LIMIT 1")))
                .map(MybatisPacketMappings::toDomain);
    }

    @Override
    public void insert(PacketDraft draft) {
        mapper.insert(MybatisPacketMappings.toEntity(draft));
    }

    @Override
    public int markStaleByScopeRevision(UUID taskId, UUID scopeRevisionId, UUID actorUserId,
            java.time.Instant updatedAt) {
        return mapper.markStaleByScopeRevision(cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(taskId),
                cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(scopeRevisionId),
                cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid.toStorage(actorUserId), updatedAt);
    }
}
