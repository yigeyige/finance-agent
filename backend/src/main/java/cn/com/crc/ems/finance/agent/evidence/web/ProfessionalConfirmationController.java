package cn.com.crc.ems.finance.agent.evidence.web;

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

import cn.com.crc.ems.finance.agent.evidence.application.ProfessionalConfirmationApplicationService;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import jakarta.validation.Valid;

/** 逐条专业判断确认 REST 入口。 */
@RestController
@RequestMapping("/api/v1/tasks")
public final class ProfessionalConfirmationController {

    private final ProfessionalConfirmationApplicationService confirmationService;

    public ProfessionalConfirmationController(ProfessionalConfirmationApplicationService confirmationService) {
        this.confirmationService = confirmationService;
    }

    /** 保存一条专业确认，禁止查看者和批量无理由确认。 */
    @PostMapping("/{taskId}/findings/{findingId}/professional-confirmations")
    public ResponseEntity<ProfessionalConfirmationResponse> confirm(@PathVariable UUID taskId,
            @PathVariable UUID findingId, @Valid @RequestBody ProfessionalConfirmationRequest request,
            @RequestHeader(value = "X-Actor-User-Id", required = false) UUID legacyActorUserId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        UUID actorUserId = trustedOperator(legacyActorUserId);
        var result = confirmationService.confirm(taskId, findingId, actorUserId, request.scopeRevisionId(),
                request.decision(), request.rationale(), request.evidenceRefs());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProfessionalConfirmationResponse.from(result));
    }

    private static UUID trustedOperator(UUID legacyActorUserId) {
        var identity = IdentityContext.current();
        if (identity != null) {
            if (!identity.hasRole(RoleCode.OPERATOR)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权进行专业确认");
            }
            return identity.userId();
        }
        if (legacyActorUserId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "缺少专业确认身份");
        }
        return legacyActorUserId;
    }

    private static void requireIdempotencyKey(String value) {
        if (value == null || value.isBlank() || value.length() < 16 || value.length() > 200) {
            throw new IllegalArgumentException("Idempotency-Key must contain 16 to 200 characters");
        }
    }
}
