package cn.com.crc.ems.finance.agent.packet.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.application.PacketReviewResult;

/**
 *
 * @Description: 草稿复核响应，回显被复核摘要和绑定。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PacketReviewResponse(UUID taskId, UUID draftId, String contentDigest, UUID scopeRevisionId,
        UUID runtimeBundleId, String decision, UUID reviewerUserId, Instant reviewedAt, String comment) {

    public static PacketReviewResponse from(PacketReviewResult result) {
        var review = result.review();
        return new PacketReviewResponse(result.draft().taskId(), review.draftId(), review.contentDigest(),
                review.scopeRevisionId(), review.runtimeBundleId(), review.decision().name(), review.reviewerUserId(),
                review.reviewedAt(), review.comment());
    }
}
