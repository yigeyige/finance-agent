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
import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;
import cn.com.crc.ems.finance.agent.packet.application.PacketPublicationCommand;
import cn.com.crc.ems.finance.agent.packet.application.PacketPublicationService;
import cn.com.crc.ems.finance.agent.packet.application.ScopeSnapshot;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import jakarta.validation.Valid;

/** 发布前全量重查与原子发布 REST 入口。 */
@RestController
@RequestMapping("/api/v1/tasks")
public final class PacketPublicationController {

    private final PacketPublicationService publicationService;

    public PacketPublicationController(PacketPublicationService publicationService) {
        this.publicationService = publicationService;
    }

    /** 触发范围重查，并在无变化时写入不可变发布修订。 */
    @PostMapping("/{taskId}/packet-publications")
    public ResponseEntity<PacketPublicationResponse> publish(@PathVariable UUID taskId,
            @Valid @RequestBody PacketPublicationRequest request,
            @RequestHeader(value = "X-Actor-User-Id", required = false) UUID legacyActorUserId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        UUID actor = trustedOperator(legacyActorUserId);
        var result = publicationService.publish(new PacketPublicationCommand(taskId, request.draftId(),
                request.publicationId(), request.contentDigest(), request.scopeRevisionId(), request.runtimeBundleId(),
                request.canonicalJson(), request.htmlSnapshot(), request.sourceVersions(), request.coverageSummary(),
                snapshots(request.baseline()), snapshots(request.current()), actor));
        return ResponseEntity.status(result.published() ? HttpStatus.CREATED : HttpStatus.CONFLICT)
                .body(PacketPublicationResponse.from(taskId, result));
    }

    private static UUID trustedOperator(UUID legacyActorUserId) {
        var identity = IdentityContext.current();
        if (identity != null) {
            if (!identity.hasRole(RoleCode.OPERATOR)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权发布决策包");
            }
            return identity.userId();
        }
        if (legacyActorUserId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "缺少决策包发布身份");
        }
        return legacyActorUserId;
    }

    private static void requireIdempotencyKey(String value) {
        if (value == null || value.isBlank() || value.length() < 16 || value.length() > 200) {
            throw new IllegalArgumentException("Idempotency-Key must contain 16 to 200 characters");
        }
    }

    /** 将 HTTP 层 Jackson 3 节点严格转换为发布重查所需的内部快照。 */
    private static java.util.List<ScopeSnapshot> snapshots(java.util.List<tools.jackson.databind.JsonNode> values) {
        return values.stream().map(PacketPublicationController::snapshot).toList();
    }

    /** 读取单个完整范围快照，缺失字段由领域构造器拒绝。 */
    private static ScopeSnapshot snapshot(tools.jackson.databind.JsonNode value) {
        var json = CanonicalJsonCodec.parse(value.toString());
        return new ScopeSnapshot(json.path("objectKey").asText(), json.path("fieldsDigest").asText(),
                EffectiveClass.valueOf(json.path("effectiveClass").asText()), json.path("sourceVersion").asText(),
                json.path("ruleSetVersion").asText(), json.path("templateVersion").asText());
    }
}
