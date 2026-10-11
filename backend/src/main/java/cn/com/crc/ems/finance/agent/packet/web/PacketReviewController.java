package cn.com.crc.ems.finance.agent.packet.web;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.packet.application.PacketReviewApplicationService;
import jakarta.validation.Valid;

/**
 *
 * @Description: 决策包草稿复核 REST 入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@RestController
@RequestMapping("/api/v1/tasks")
public final class PacketReviewController {

    private final PacketReviewApplicationService reviewService;

    public PacketReviewController(PacketReviewApplicationService reviewService) {
        this.reviewService = reviewService;
    }

    /** 追加复核记录，批准动作只接受完整专业确认链。 */
    @PostMapping("/{taskId}/packet-drafts/{draftId}/reviews")
    public ResponseEntity<PacketReviewResponse> review(@PathVariable UUID taskId, @PathVariable UUID draftId,
            @Valid @RequestBody PacketReviewRequest request,
            @RequestHeader(value = "X-Actor-User-Id", required = false) UUID legacyReviewerUserId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        UUID reviewer = trustedOperator(legacyReviewerUserId);
        var result = reviewService.review(taskId, draftId, reviewer, request.contentDigest(),
                request.scopeRevisionId(), request.runtimeBundleId(), request.decision(), request.comment());
        return ResponseEntity.status(HttpStatus.CREATED).body(PacketReviewResponse.from(result));
    }

    private static UUID trustedOperator(UUID legacyReviewerUserId) {
        var identity = IdentityContext.current();
        if (identity != null) {
            if (!identity.hasRole(RoleCode.OPERATOR)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权复核决策包");
            }
            return identity.userId();
        }
        if (legacyReviewerUserId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "缺少决策包复核身份");
        }
        return legacyReviewerUserId;
    }

    private static void requireIdempotencyKey(String value) {
        if (value == null || value.isBlank() || value.length() < 16 || value.length() > 200) {
            throw new IllegalArgumentException("Idempotency-Key must contain 16 to 200 characters");
        }
    }
}
