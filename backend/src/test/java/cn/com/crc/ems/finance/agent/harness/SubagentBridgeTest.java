package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.SubagentBridge;
import cn.com.crc.ems.finance.agent.harness.application.SubagentCatalog;
import cn.com.crc.ems.finance.agent.harness.application.SubagentInvocation;
import cn.com.crc.ems.finance.agent.harness.application.SubagentResult;
import cn.com.crc.ems.finance.agent.harness.application.SyntheticSubagentHandler;

class SubagentBridgeTest {

    private static final Instant NOW = Instant.parse("2026-09-29T02:40:00Z");

    @Test
    void registersThreeRolesAndIntersectsTaskApproval() {
        SubagentBridge bridge = new SubagentBridge(SubagentCatalog.defaultCatalog());
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID invocationId = UUID.randomUUID();
        SubagentInvocation request = new SubagentInvocation(invocationId, taskId, userId, "evidence-organizer",
                Set.of("read-config", "unapproved-by-task"), Set.of("SAFE_EVIDENCE"), Set.of("read-config"),
                Set.of("SAFE_EVIDENCE"), java.util.List.of("evidence-001"), false, false);

        var outcome = bridge.invoke(request);

        assertThat(SubagentCatalog.defaultCatalog().all()).hasSize(3);
        assertThat(outcome.effectivePermission().effectiveTools()).containsExactly("read-config");
        assertThat(outcome.event().eventType()).isEqualTo("SUBAGENT_COMPLETED");
        assertThat(outcome.event().taskId()).isEqualTo(taskId);
        assertThat(outcome.result().outputRefs()).containsExactly("evidence-001");
    }

    @Test
    void rejectsUnknownRoleExtraPermissionPublicationAndScopeExpansion() {
        SubagentBridge bridge = new SubagentBridge(SubagentCatalog.defaultCatalog());
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        assertThatThrownBy(() -> bridge.invoke(request(taskId, userId, "missing-role", Set.of(), Set.of(), false, false)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> bridge.invoke(request(taskId, userId, "evidence-organizer", Set.of("write-config"),
                Set.of(), false, false))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> bridge.invoke(request(taskId, userId, "evidence-organizer", Set.of(), Set.of(), true,
                false))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> bridge.invoke(request(taskId, userId, "evidence-organizer", Set.of(), Set.of(), false,
                true))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsResultOwnershipMismatchBeforePublicEvent() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        SubagentBridge bridge = new SubagentBridge(SubagentCatalog.defaultCatalog(),
                Map.of("evidence-organizer", (request, permission) -> SubagentResult.create(request.invocationId(),
                        UUID.randomUUID(), request.userId(), request.subagentId(), "wrong-owner", request.evidenceRefs())),
                Clock.fixed(NOW, ZoneOffset.UTC));

        assertThatThrownBy(() -> bridge.invoke(request(taskId, userId, "evidence-organizer", Set.of("read-config"),
                Set.of("SAFE_EVIDENCE"), false, false))).isInstanceOf(IllegalStateException.class);
    }

    private static SubagentInvocation request(UUID taskId, UUID userId, String role, Set<String> requestedTools,
            Set<String> requestedFields, boolean publish, boolean expandScope) {
        return new SubagentInvocation(UUID.randomUUID(), taskId, userId, role, Set.of("read-config"),
                Set.of("SAFE_EVIDENCE"), requestedTools, requestedFields, java.util.List.of("evidence-001"), publish,
                expandScope);
    }
}
