package cn.com.crc.ems.finance.agent.tool;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.application.ExecutionPermit;
import cn.com.crc.ems.finance.agent.task.application.StepExecutionGuard;
import cn.com.crc.ems.finance.agent.tool.application.PermitSigner;
import cn.com.crc.ems.finance.agent.tool.application.SignedToolPermit;

class ToolGatewayTest {

    @Test
    void rejectsTamperedPermit() {
        Instant now = Instant.parse("2026-09-28T00:00:00Z");
        ExecutionPermit permit = new ExecutionPermit(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), "a".repeat(64), "idem-1", now, now.plusSeconds(60));
        PermitSigner signer = new PermitSigner();
        SignedToolPermit signed = new SignedToolPermit(permit, "read-config", "v1",
                signer.sign(permit, "read-config", "v1"));
        SignedToolPermit tampered = new SignedToolPermit(permit, "read-config", "v2", signed.signature());

        assertThatThrownBy(() -> {
            if (!signer.verify(tampered)) {
                throw new IllegalStateException("tampered");
            }
        }).isInstanceOf(IllegalStateException.class);
    }
}
