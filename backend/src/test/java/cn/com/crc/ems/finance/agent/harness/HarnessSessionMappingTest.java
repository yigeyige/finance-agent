package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.HarnessPlanMapper;
import cn.com.crc.ems.finance.agent.harness.application.HarnessSessionBinding;
import cn.com.crc.ems.finance.agent.harness.application.HarnessSessionService;
import cn.com.crc.ems.finance.agent.harness.application.PlanRevisionRepository;
import cn.com.crc.ems.finance.agent.harness.application.SessionStateRepository;
import cn.com.crc.ems.finance.agent.harness.domain.PlanRevision;
import cn.com.crc.ems.finance.agent.harness.domain.SessionState;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;

class HarnessSessionMappingTest {

    private static final Instant NOW = Instant.parse("2026-09-28T00:00:00Z");
    private static final UUID USER = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TASK = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID SESSION = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID BUNDLE = UUID.fromString("00000000-0000-0000-0000-000000000004");
    private static final UUID SCOPE = UUID.fromString("00000000-0000-0000-0000-000000000005");
    private static final StateReference REF = new StateReference("harness-v1", "{\"state\":{}}",
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");

    @Test
    void sessionPlanMappingKeepsBindingAndDoesNotExecutePlan() {
        InMemorySessions sessions = new InMemorySessions();
        InMemoryPlans plans = new InMemoryPlans();
        HarnessSessionService service = new HarnessSessionService(sessions, plans,
                Clock.fixed(NOW, ZoneOffset.UTC));
        HarnessSessionBinding binding = new HarnessSessionBinding(SESSION, TASK, USER, BUNDLE, SCOPE);
        service.start(binding, "harness-v1", REF, USER, NOW);
        PlanRevision revision = service.appendPlan(binding,
                new StateReference("plan-v1", "{\"steps\":[{\"goal\":\"读取配置\",\"recommended\":true},"
                        + "{\"goal\":\"复核范围\",\"recommended\":false}]}",
                        "abcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcd"), USER, NOW);

        var command = service.mapPlan(binding, revision, USER);

        assertThat(command.taskId()).isEqualTo(TASK);
        assertThat(command.steps()).hasSize(2);
        assertThat(command.steps()).extracting("recommended").containsExactly(true, false);
        assertThat(sessions.value).isNotNull();
        assertThat(plans.values).hasSize(1);
    }

    @Test
    void crossTaskAndThreeStepPlansFailBeforeAnyExecution() {
        HarnessSessionBinding binding = new HarnessSessionBinding(SESSION, TASK, USER, BUNDLE, SCOPE);
        PlanRevision plan = new PlanRevision(UUID.randomUUID(), SESSION, UUID.randomUUID(), USER, 1,
                new StateReference("plan-v1", "{\"steps\":[{\"goal\":\"a\",\"recommended\":true},"
                        + "{\"goal\":\"b\",\"recommended\":false},{\"goal\":\"c\",\"recommended\":false},"
                        + "{\"goal\":\"d\",\"recommended\":false}]}",
                        "abcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcd"), USER, NOW, USER, NOW);
        assertThatThrownBy(() -> HarnessPlanMapper.toStepProposal(plan, binding, USER, NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("outside");
    }

    private static final class InMemorySessions implements SessionStateRepository {
        private SessionState value;

        @Override
        public Optional<SessionState> findById(UUID sessionId, UUID taskId, UUID userId) {
            return value != null && value.sessionId().equals(sessionId) && value.taskId().equals(taskId)
                    && value.userId().equals(userId) ? Optional.of(value) : Optional.empty();
        }

        @Override
        public void insert(SessionState sessionState) {
            value = sessionState;
        }

        @Override
        public boolean updateState(UUID sessionId, UUID taskId, UUID userId, StateReference stateRef,
                UUID updatedBy, Instant updatedAt) {
            return false;
        }
    }

    private static final class InMemoryPlans implements PlanRevisionRepository {
        private final List<PlanRevision> values = new ArrayList<>();

        @Override
        public List<PlanRevision> findBySessionId(UUID sessionId, UUID taskId, UUID userId) {
            return values.stream().filter(value -> value.sessionId().equals(sessionId)
                    && value.taskId().equals(taskId) && value.userId().equals(userId)).toList();
        }

        @Override
        public void insert(PlanRevision planRevision) {
            values.add(planRevision);
        }
    }
}
