package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.CheckpointRepository;
import cn.com.crc.ems.finance.agent.harness.domain.Checkpoint;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;

class HarnessDomainModelTest {

    private static final UUID SESSION_ID = UUID.randomUUID();
    private static final UUID TASK_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STEP_ID = UUID.randomUUID();
    private static final UUID SCOPE_ID = UUID.randomUUID();
    private static final UUID APPROVAL_ID = UUID.randomUUID();
    private static final Instant NOW = Instant.parse("2026-09-27T00:00:00Z");
    private static final String DIGEST = "a".repeat(64);

    @Test
    void stateReferenceRejectsForbiddenFieldsAndInvalidDigest() {
        assertThatThrownBy(() -> new StateReference("harness-v1", "{\"reasoning_content\":\"secret\"}", DIGEST))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new StateReference("harness-v1", "{\"safe\":true}", "short"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new StateReference("harness-v1", "{\"step\":\"s1\"}", DIGEST))
                .doesNotThrowAnyException();
    }

    @Test
    void checkpointRejectsStaleScopeOrApprovalContext() {
        Checkpoint checkpoint = checkpoint(SCOPE_ID, APPROVAL_ID);
        assertThatCode(() -> checkpoint.requireCompatible(TASK_ID, USER_ID, SESSION_ID, STEP_ID, SCOPE_ID, APPROVAL_ID))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> checkpoint.requireCompatible(TASK_ID, USER_ID, SESSION_ID, STEP_ID,
                UUID.randomUUID(), APPROVAL_ID)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> checkpoint.requireCompatible(TASK_ID, USER_ID, SESSION_ID, STEP_ID,
                SCOPE_ID, UUID.randomUUID())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void checkpointRepositoryRequiresExplicitIsolationParameters() {
        assertThat(CheckpointRepository.class.getMethods())
                .extracting(java.lang.reflect.Method::getName)
                .containsExactlyInAnyOrder("findById", "findBySessionId", "insert");
        assertThat(CheckpointRepository.class.getMethods())
                .filteredOn(method -> method.getName().startsWith("find"))
                .allSatisfy(method -> org.assertj.core.api.Assertions.assertThat(method.getParameterCount()).isGreaterThanOrEqualTo(3));
    }

    private static Checkpoint checkpoint(UUID scopeRevisionId, UUID approvalId) {
        return new Checkpoint(UUID.randomUUID(), SESSION_ID, TASK_ID, USER_ID, STEP_ID, scopeRevisionId,
                approvalId, "STEP_COMPLETED", new StateReference("harness-v1", "{\"step\":\"s1\"}", DIGEST),
                USER_ID, NOW, USER_ID, NOW);
    }
}
