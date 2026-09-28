package cn.com.crc.ems.finance.agent.tool.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/** 将 Harness permission 暂停、人工决定和业务 ToolGateway 二次守卫串成单向桥。 */
@Service
public class HitlToolBridge {

    private static final Duration REQUEST_TTL = Duration.ofMinutes(10);
    private final ToolGateway toolGateway;
    private final HarnessPermissionMiddleware permission;
    private final Clock clock;
    private final Map<UUID, PendingRequest> pending = new ConcurrentHashMap<>();

    @Autowired
    public HitlToolBridge(ToolGateway toolGateway, Clock clock) {
        this(toolGateway, new HarnessPermissionMiddleware(io.agentscope.core.permission.PermissionMode.DEFAULT), clock);
    }

    public HitlToolBridge(ToolGateway toolGateway, HarnessPermissionMiddleware permission, Clock clock) {
        this.toolGateway = Objects.requireNonNull(toolGateway, "toolGateway is required");
        this.permission = Objects.requireNonNull(permission, "permission is required");
        this.clock = Objects.requireNonNull(clock, "clock is required");
    }

    /** 请求先暂停到 HITL，不调用业务网关。 */
    public HitlBridgeResult request(HarnessToolRequest request) {
        Objects.requireNonNull(request, "request is required");
        Instant now = Instant.now(clock);
        if (permission.check(request.toolCode()) == HarnessPermissionDecision.DENIED) {
            return new HitlBridgeResult(new HitlEvent(request.requestId(), request.toolCode(), HitlEventType.DENIED,
                    "HARNESS_PERMISSION_DENIED", now), null);
        }
        pending.put(request.requestId(), new PendingRequest(request, now.plus(REQUEST_TTL)));
        return new HitlBridgeResult(new HitlEvent(request.requestId(), request.toolCode(), HitlEventType.WAITING_APPROVAL,
                "HITL_REQUIRED", now), null);
    }

    /** 人工批准后仍必须经过业务 ToolGateway，HITL 本身不能签发 permit。 */
    public HitlBridgeResult approve(UUID requestId) {
        PendingRequest pendingRequest = take(requestId);
        Instant now = Instant.now(clock);
        if (pendingRequest == null) {
            return result(requestId, "UNKNOWN", HitlEventType.REJECTED, "HITL_REQUEST_NOT_FOUND", now);
        }
        if (!now.isBefore(pendingRequest.expiresAt())) {
            return result(requestId, pendingRequest.request().toolCode(), HitlEventType.EXPIRED,
                    "HITL_REQUEST_EXPIRED", now);
        }
        SignedToolPermit permit = toolGateway.authorize(pendingRequest.request().guardRequest());
        return new HitlBridgeResult(new HitlEvent(requestId, pendingRequest.request().toolCode(), HitlEventType.APPROVED,
                "BUSINESS_GATE_PASSED", now), permit);
    }

    /** 拒绝请求并丢弃待审批参数，不触达业务网关。 */
    public HitlBridgeResult reject(UUID requestId) {
        PendingRequest pendingRequest = take(requestId);
        return result(requestId, pendingRequest == null ? "UNKNOWN" : pendingRequest.request().toolCode(),
                HitlEventType.REJECTED, "HITL_REJECTED", Instant.now(clock));
    }

    private PendingRequest take(UUID requestId) {
        if (requestId == null) {
            throw new IllegalArgumentException("requestId is required");
        }
        return pending.remove(requestId);
    }

    private static HitlBridgeResult result(UUID requestId, String toolCode, HitlEventType type,
            String code, Instant occurredAt) {
        return new HitlBridgeResult(new HitlEvent(requestId, toolCode, type, code, occurredAt), null);
    }

    private record PendingRequest(HarnessToolRequest request, Instant expiresAt) {
    }
}
