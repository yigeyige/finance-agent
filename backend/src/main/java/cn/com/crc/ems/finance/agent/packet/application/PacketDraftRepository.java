package cn.com.crc.ems.finance.agent.packet.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.domain.PacketDraft;

/**
 *
 * @Description: 决策包草稿的追加与查询端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface PacketDraftRepository {

    /** 按草稿 ID 查询。 */
    Optional<PacketDraft> findById(UUID draftId);

    /** 查询任务当前草稿。 */
    Optional<PacketDraft> findCurrentByTaskId(UUID taskId);

    /** 查询任务最新草稿，包括已 stale 的历史版本。 */
    Optional<PacketDraft> findLatestByTaskId(UUID taskId);

    /** 追加草稿，不提供原位修改。 */
    void insert(PacketDraft draft);

    /** 将任务旧范围下的当前草稿标记为 stale。 */
    int markStaleByScopeRevision(UUID taskId, UUID scopeRevisionId, UUID actorUserId,
            java.time.Instant updatedAt);
}
