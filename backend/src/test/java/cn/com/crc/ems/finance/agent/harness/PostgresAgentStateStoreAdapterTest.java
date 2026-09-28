package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.crc.ems.finance.agent.harness.application.SessionStateRepository;
import cn.com.crc.ems.finance.agent.harness.domain.SessionState;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;
import cn.com.crc.ems.finance.agent.harness.infrastructure.JacksonAgentStateCodec;
import cn.com.crc.ems.finance.agent.harness.infrastructure.PostgresAgentStateStoreAdapter;
import io.agentscope.core.state.State;

class PostgresAgentStateStoreAdapterTest {

    private static final Instant NOW = Instant.parse("2026-09-28T00:00:00Z");
    private static final UUID USER = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TASK = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID SESSION = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final String DIGEST = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    @Test
    void savesAndRestoresStateByUserSessionAndTaskBoundary() {
        InMemorySessions repository = new InMemorySessions(session("harness-v1", "{\"state\":{}}", DIGEST));
        var adapter = new PostgresAgentStateStoreAdapter(repository,
                new JacksonAgentStateCodec(new ObjectMapper()), Clock.fixed(NOW, ZoneOffset.UTC));

        adapter.save(USER.toString(), USER.toString(), SESSION.toString(), new SmokeState("checkpoint-1"));
        Optional<SmokeState> restored = adapter.get(USER.toString(), USER.toString(), SESSION.toString(), SmokeState.class);

        assertThat(restored).contains(new SmokeState("checkpoint-1"));
        assertThat(adapter.exists(USER.toString(), SESSION.toString())).isTrue();
        assertThat(adapter.listSessionIds(USER.toString())).containsExactly(SESSION.toString());
    }

    @Test
    void rejectsCrossUserCorruptAndStaleRecovery() {
        InMemorySessions repository = new InMemorySessions(session("harness-v1", "{\"state\":{}}", DIGEST));
        var adapter = new PostgresAgentStateStoreAdapter(repository,
                new JacksonAgentStateCodec(new ObjectMapper()), Clock.fixed(NOW, ZoneOffset.UTC));
        assertThatThrownBy(() -> adapter.get(USER.toString(), UUID.randomUUID().toString(), SESSION.toString(), SmokeState.class))
                .isInstanceOf(IllegalArgumentException.class);

        repository.value = session("harness-v1", "{\"state\":{}}", "abcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcd");
        assertThatThrownBy(() -> adapter.get(USER.toString(), USER.toString(), SESSION.toString(), SmokeState.class))
                .isInstanceOf(IllegalStateException.class);

        repository.value = session("old-runtime", "{\"state\":{}}", DIGEST);
        assertThatThrownBy(() -> adapter.get(USER.toString(), USER.toString(), SESSION.toString(), SmokeState.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("STALE");
    }

    private static SessionState session(String harnessVersion, String json, String digest) {
        return new SessionState(SESSION, TASK, USER, harnessVersion, new StateReference("harness-v1", json, digest),
                USER, NOW, USER, NOW);
    }

    private record SmokeState(String marker) implements State {
    }

    private static final class InMemorySessions implements SessionStateRepository {
        private SessionState value;

        private InMemorySessions(SessionState value) {
            this.value = value;
        }

        @Override
        public Optional<SessionState> findById(UUID sessionId, UUID taskId, UUID userId) {
            return Optional.of(value);
        }

        @Override
        public Optional<SessionState> findBySessionIdAndUserId(UUID sessionId, UUID userId) {
            return value.sessionId().equals(sessionId) && value.userId().equals(userId) ? Optional.of(value) : Optional.empty();
        }

        @Override
        public void insert(SessionState state) {
            value = state;
        }

        @Override
        public boolean updateState(UUID sessionId, UUID taskId, UUID userId, StateReference stateRef,
                UUID updatedBy, Instant updatedAt) {
            value = new SessionState(value.sessionId(), value.taskId(), value.userId(), value.harnessVersion(),
                    stateRef, value.createdBy(), value.createdAt(), updatedBy, updatedAt);
            return true;
        }

        @Override
        public boolean delete(UUID sessionId, UUID taskId, UUID userId) {
            return true;
        }

        @Override
        public Set<UUID> listSessionIds(UUID userId) {
            return Set.of(value.sessionId());
        }
    }
}
