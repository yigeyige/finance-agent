package cn.com.crc.ems.finance.agent.packet.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.domain.PacketReview;

/** 决策包复核的追加与查询端口。 */
public interface PacketReviewRepository {

    /** 按复核 ID 查询。 */
    Optional<PacketReview> findById(UUID reviewId);

    /** 查询草稿的复核记录。 */
    Optional<PacketReview> findByDraftId(UUID draftId);

    /** 追加复核记录。 */
    void insert(PacketReview review);
}
