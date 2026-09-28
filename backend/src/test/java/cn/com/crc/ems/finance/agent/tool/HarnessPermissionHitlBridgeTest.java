package cn.com.crc.ems.finance.agent.tool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.agentscope.core.permission.PermissionMode;

import cn.com.crc.ems.finance.agent.task.application.ExecutionPermit;
import cn.com.crc.ems.finance.agent.task.application.StepExecutionRequest;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.tool.application.HarnessPermissionMiddleware;
import cn.com.crc.ems.finance.agent.tool.application.HarnessToolRequest;
import cn.com.crc.ems.finance.agent.tool.application.HitlEventType;
import cn.com.crc.ems.finance.agent.tool.application.HitlToolBridge;
import cn.com.crc.ems.finance.agent.tool.application.SignedToolPermit;
import cn.com.crc.ems.finance.agent.tool.application.ToolGateway;
import cn.com.crc.ems.finance.agent.tool.application.ToolGuardRequest;

class HarnessPermissionHitlBridgeTest {

    private static final Instant NOW = Instant.parse("2026-09-28T00:00:00Z");
    private static final UUID TASK = UUID.randomUUID();
    private static final UUID STEP = UUID.randomUUID();
    private static final UUID APPROVAL = UUID.randomUUID();
    private static final UUID SCOPE = UUID.randomUUID();
    private static final UUID SOURCE = UUID.randomUUID();
    private static final UUID BUNDLE = UUID.randomUUID();
    private static final String DIGEST = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    @Test
    void requestPausesThenApprovalStillRequiresBusinessGateway() {
        ToolGateway gateway = mock(ToolGateway.class);
        SignedToolPermit permit = new SignedToolPermit(new ExecutionPermit(TASK, STEP, APPROVAL, SCOPE, SOURCE,
                DIGEST, "idem-1", NOW, NOW.plusSeconds(60)), "read-config", "v1", "signature");
        when(gateway.authorize(org.mockito.ArgumentMatchers.any())).thenReturn(permit);
        HitlToolBridge bridge = new HitlToolBridge(gateway,
                new HarnessPermissionMiddleware(PermissionMode.DEFAULT), Clock.fixed(NOW, ZoneOffset.UTC));
        HarnessToolRequest request = request("idem-1");

        var waiting = bridge.request(request);
        var approved = bridge.approve(request.requestId());

        assertThat(waiting.event().type()).isEqualTo(HitlEventType.WAITING_APPROVAL);
        assertThat(approved.event().type()).isEqualTo(HitlEventType.APPROVED);
        assertThat(approved.permit()).isSameAs(permit);
        verify(gateway).authorize(request.guardRequest());
    }

    @Test
    void bypassModeCannotSkipHitlOrBusinessApproval() {
        ToolGateway gateway = mock(ToolGateway.class);
        HitlToolBridge bridge = new HitlToolBridge(gateway,
                new HarnessPermissionMiddleware(PermissionMode.BYPASS), Clock.fixed(NOW, ZoneOffset.UTC));

        var result = bridge.request(request("idem-2"));

        assertThat(result.event().type()).isEqualTo(HitlEventType.DENIED);
        verifyNoInteractions(gateway);
    }

    @Test
    void rejectionDropsPendingRequestAndNeverCallsGateway() {
        ToolGateway gateway = mock(ToolGateway.class);
        HitlToolBridge bridge = new HitlToolBridge(gateway,
                new HarnessPermissionMiddleware(PermissionMode.DEFAULT), Clock.fixed(NOW, ZoneOffset.UTC));
        HarnessToolRequest request = request("idem-3");

        bridge.request(request);
        var rejected = bridge.reject(request.requestId());
        var missing = bridge.approve(request.requestId());

        assertThat(rejected.event().type()).isEqualTo(HitlEventType.REJECTED);
        assertThat(missing.event().type()).isEqualTo(HitlEventType.REJECTED);
        verifyNoInteractions(gateway);
    }

    private static HarnessToolRequest request(String idempotencyKey) {
        StepExecutionRequest execution = new StepExecutionRequest(TASK, STEP, APPROVAL, SCOPE, SOURCE, DIGEST,
                "fields-v1", "query-v1", idempotencyKey, NOW);
        ToolGuardRequest guard = new ToolGuardRequest(BUNDLE, "read-config", "v1", TaskStatus.RUNNING_STEP,
                "local", execution);
        return new HarnessToolRequest(UUID.randomUUID(), "read-config", guard);
    }
}
