package cn.com.crc.ems.finance.agent.packet.web;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.packet.application.PacketHandoffCommand;
import cn.com.crc.ems.finance.agent.packet.application.PacketLifecycleService;
import cn.com.crc.ems.finance.agent.packet.application.TaskClosureCommand;
import jakarta.validation.Valid;

/** 提供已发布决策包查看、交接和调查关闭 REST 入口。 */
@RestController
@RequestMapping("/api/v1/tasks")
public final class PacketLifecycleController {

    private final PacketLifecycleService lifecycleService;

    public PacketLifecycleController(PacketLifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    /** OPERATOR 与 VIEWER 共用同一份最新已发布修订视图。 */
    @GetMapping("/{taskId}/published-packet")
    public PublishedPacketResponse published(@PathVariable UUID taskId) {
        requireViewerOrOperator();
        return PublishedPacketResponse.from(lifecycleService.publishedView(taskId));
    }

    /** OPERATOR 登记外部责任与流程引用，并将任务交接。 */
    @PostMapping("/{taskId}/packet-revisions/{revisionId}/handoff")
    public ResponseEntity<PacketHandoffResponse> handoff(@PathVariable UUID taskId,
            @PathVariable UUID revisionId, @Valid @RequestBody PacketHandoffRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        var identity = requireOperator();
        var result = lifecycleService.handoff(new PacketHandoffCommand(taskId, revisionId, request.ownerReference(),
                request.externalProcessReference(), request.downstreamStatus(), identity.userId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(PacketHandoffResponse.from(result));
    }

    /** OPERATOR 关闭已交接调查；整改完成需要另行确认。 */
    @PostMapping("/{taskId}/close")
    public ResponseEntity<TaskClosureResponse> close(@PathVariable UUID taskId,
            @Valid @RequestBody TaskClosureRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        var identity = requireOperator();
        var result = lifecycleService.close(new TaskClosureCommand(taskId, request.closureReason(), identity.userId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskClosureResponse.from(result));
    }

    private static void requireViewerOrOperator() {
        var identity = IdentityContext.require();
        if (!identity.hasRole(RoleCode.OPERATOR) && !identity.hasRole(RoleCode.VIEWER)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权查看已发布决策包");
        }
    }

    private static cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity requireOperator() {
        var identity = IdentityContext.require();
        if (!identity.hasRole(RoleCode.OPERATOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权交接或关闭调查");
        }
        return identity;
    }

    private static void requireIdempotencyKey(String value) {
        if (value == null || value.isBlank() || value.length() < 16 || value.length() > 200) {
            throw new IllegalArgumentException("Idempotency-Key must contain 16 to 200 characters");
        }
    }
}
