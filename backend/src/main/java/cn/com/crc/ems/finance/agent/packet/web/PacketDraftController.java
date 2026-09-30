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
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
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
                request.runtimeBundleId(), request.problem(), json(request.premises()), json(request.scope()),
                json(request.sources()), json(request.facts()), json(request.computations()),
                json(request.professionalJudgments()), json(request.recommendations()),
                json(request.businessConfirmations()), json(request.validationPreparation()), json(request.handoff()), actor));
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

    /**
     *
     * @description: 将 Spring MVC 的 Jackson 3 请求节点转换为应用层既有 Jackson 2 节点，边界仅通过 JSON 文本交接。
     *
     * @param: value HTTP 请求 JSON 节点。
     * @return: 应用层可进行严格校验和规范化的 JSON 节点。
     * @author: yigeyige99
     */
    private static com.fasterxml.jackson.databind.JsonNode json(tools.jackson.databind.JsonNode value) {
        return CanonicalJsonCodec.parse(value.toString());
    }
}
