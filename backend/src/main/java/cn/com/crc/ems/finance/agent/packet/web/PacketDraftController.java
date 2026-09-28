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
import cn.com.crc.ems.finance.agent.packet.application.PacketAssemblyCommand;
import cn.com.crc.ems.finance.agent.packet.application.PacketDraftAssembler;
import jakarta.validation.Valid;

/** 决策包草稿组装 REST 入口。 */
@RestController
@RequestMapping("/api/v1/tasks")
public final class PacketDraftController {

    private final PacketDraftAssembler assembler;

    public PacketDraftController(PacketDraftAssembler assembler) {
        this.assembler = assembler;
    }

    /** 组装结构化草稿并返回 canonical JSON 与 HTML 快照。 */
    @PostMapping("/{taskId}/packet-drafts")
    public ResponseEntity<PacketDraftResponse> assemble(@PathVariable UUID taskId,
            @Valid @RequestBody PacketDraftRequest request,
            @RequestHeader(value = "X-Actor-User-Id", required = false) UUID legacyActorUserId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        UUID actor = trustedOperator(legacyActorUserId);
        var result = assembler.assemble(new PacketAssemblyCommand(taskId, request.scopeRevisionId(),
                request.runtimeBundleId(), request.problem(), request.premises(), request.scope(), request.sources(),
                request.facts(), request.computations(), request.professionalJudgments(), request.recommendations(),
                request.businessConfirmations(), request.validationPreparation(), request.handoff(), actor));
        return ResponseEntity.status(HttpStatus.CREATED).body(PacketDraftResponse.from(result));
    }

    private static UUID trustedOperator(UUID legacyActorUserId) {
        var identity = IdentityContext.current();
        if (identity != null) {
            if (!identity.hasRole(RoleCode.OPERATOR)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权组装决策包");
            }
            return identity.userId();
        }
        if (legacyActorUserId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "缺少决策包组装身份");
        }
        return legacyActorUserId;
    }

    private static void requireIdempotencyKey(String value) {
        if (value == null || value.isBlank() || value.length() < 16 || value.length() > 200) {
            throw new IllegalArgumentException("Idempotency-Key must contain 16 to 200 characters");
        }
    }
}
