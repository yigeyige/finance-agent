package cn.com.crc.ems.finance.agent.packet.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.domain.PacketDraft;

/** 决策包草稿的追加与查询端口。 */
public interface PacketDraftRepository {

    /** 按草稿 ID 查询。 */
    Optional<PacketDraft> findById(UUID draftId);

    /** 查询任务当前草稿。 */
    Optional<PacketDraft> findCurrentByTaskId(UUID taskId);

    /** 追加草稿，不提供原位修改。 */
    void insert(PacketDraft draft);

    /** 将任务旧范围下的当前草稿标记为 stale。 */
    int markStaleByScopeRevision(UUID taskId, UUID scopeRevisionId, UUID actorUserId,
            java.time.Instant updatedAt);
}
