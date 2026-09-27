package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.domain.TaskLease;

class TaskLeaseDomainTest {

    private static final Instant NOW = Instant.parse("2026-09-28T02:00:00Z");

    @Test
    void expiryUsesStrictBeforeBoundaryAndReleasedLeasesAreNeverExpired() {
        TaskLease lease = new TaskLease(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "worker-1", NOW,
                NOW, NOW.plusSeconds(30), null, null, NOW, null, NOW);

        assertThat(lease.isExpiredAt(NOW.plusSeconds(30))).isFalse();
        assertThat(lease.isExpiredAt(NOW.plusSeconds(31))).isTrue();
        TaskLease released = new TaskLease(lease.leaseId(), lease.taskId(), lease.stepId(), lease.ownerInstance(),
                lease.acquiredAt(), lease.heartbeatAt(), lease.expiresAt(), NOW.plusSeconds(31), null, NOW, null, NOW);
        assertThat(released.isExpiredAt(NOW.plusSeconds(31))).isFalse();
    }

    @Test
    void expiryCannotPrecedeHeartbeat() {
        assertThatThrownBy(() -> new TaskLease(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "worker-1",
                NOW, NOW, NOW.minusSeconds(1), null, null, NOW, null, NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expiresAt must not be before heartbeatAt");
    }
}
