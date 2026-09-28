package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.HarnessIdentity;
import cn.com.crc.ems.finance.agent.harness.infrastructure.HarnessAgentFactory;

class HarnessAgentSmokeContractTest {

    @Test
    void factoryBuildsLocalHarnessAndPreservesIdentityBoundary() throws Exception {
        var factory = new HarnessAgentFactory();
        var workspace = Files.createTempDirectory("finance-harness-smoke-");
        var identity = new HarnessIdentity("user-1", "session-1");
        try (var agent = factory.create(identity, workspace)) {
            var context = factory.runtimeContext(identity);
            var first = agent.call("smoke", context).block(Duration.ofSeconds(5));
            var resumed = agent.call("resume", context).block(Duration.ofSeconds(5));
            assertThat(agent.getModel().getModelName()).isEqualTo("agentscope-local-stub-v1");
            assertThat(agent.getDefaultSessionId()).isEqualTo(identity.sessionId());
            assertThat(context.getUserId()).isEqualTo(identity.userId());
            assertThat(context.getSessionId()).isEqualTo(identity.sessionId());
            assertThat(first).isNotNull();
            assertThat(resumed).isNotNull();
            assertThat(agent.getWorkspaceManager().getWorkspace()).isEqualTo(workspace.toAbsolutePath().normalize());
        }
    }

    @Test
    void missingOrUnsafeIdentityIsRejectedBeforeAgentCreation() {
        assertThatThrownBy(() -> new HarnessIdentity("", "session-1"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new HarnessIdentity("user-1", "../session"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(UUID.randomUUID()).isNotNull();
    }
}
