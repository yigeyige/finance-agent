package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.crc.ems.finance.agent.harness.application.AgentStateCodec;
import cn.com.crc.ems.finance.agent.harness.application.SessionStateRepository;
import cn.com.crc.ems.finance.agent.harness.domain.SessionState;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import io.agentscope.core.state.AgentStateStore;
import io.agentscope.core.state.State;

/**
 * 通过现有 Harness session 仓储适配 AgentScope AgentStateStore；业务 task 仍是状态权威。
 */
@Component
public class PostgresAgentStateStoreAdapter implements AgentStateStore {

    private final SessionStateRepository sessions;
    private final AgentStateCodec codec;
    private final Clock clock;
    private final String harnessVersion;

    @Autowired
    public PostgresAgentStateStoreAdapter(SessionStateRepository sessions, AgentStateCodec codec,
            Clock clock) {
        this(sessions, codec, clock, "harness-v1");
    }

    public PostgresAgentStateStoreAdapter(SessionStateRepository sessions, AgentStateCodec codec,
            Clock clock, String harnessVersion) {
        this.sessions = Objects.requireNonNull(sessions, "sessions is required");
        this.codec = Objects.requireNonNull(codec, "codec is required");
        this.clock = Objects.requireNonNull(clock, "clock is required");
        if (harnessVersion == null || harnessVersion.isBlank()) {
            throw new IllegalArgumentException("harnessVersion must not be blank");
        }
        this.harnessVersion = harnessVersion;
    }

    @Override
    public void save(String agentId, String userId, String sessionId, State state) {
        SessionState existing = requireSession(agentId, userId, sessionId);
        StateReference reference = codec.encode(state);
        if (!sessions.updateState(existing.sessionId(), existing.taskId(), existing.userId(), reference,
                existing.userId(), Instant.now(clock))) {
            throw new IllegalStateException("STATE_UPDATE_CONFLICT");
        }
    }

    @Override
    public void save(String agentId, String userId, String sessionId, List<? extends State> states) {
        if (states == null || states.size() != 1) {
            throw new IllegalArgumentException("PostgreSQL session state requires exactly one state");
        }
        save(agentId, userId, sessionId, states.get(0));
    }

    @Override
    public <T extends State> Optional<T> get(String agentId, String userId, String sessionId,
            Class<T> stateType) {
        SessionState existing = requireSession(agentId, userId, sessionId);
        return Optional.of(codec.decode(existing.stateRef(), stateType));
    }

    @Override
    public <T extends State> List<T> getList(String agentId, String userId, String sessionId,
            Class<T> stateType) {
        return get(agentId, userId, sessionId, stateType).stream().toList();
    }

    @Override
    public boolean exists(String agentId, String sessionId) {
        UUID user = parseId(agentId, "agentId");
        UUID session = parseId(sessionId, "sessionId");
        return sessions.findBySessionIdAndUserId(session, user)
                .filter(value -> harnessVersion.equals(value.harnessVersion())).isPresent();
    }

    @Override
    public void delete(String agentId, String sessionId) {
        UUID user = parseId(agentId, "agentId");
        UUID session = parseId(sessionId, "sessionId");
        SessionState existing = sessions.findBySessionIdAndUserId(session, user)
                .orElseThrow(() -> new IllegalStateException("STATE_SESSION_NOT_FOUND"));
        if (!sessions.delete(existing.sessionId(), existing.taskId(), existing.userId())) {
            throw new IllegalStateException("STATE_DELETE_CONFLICT");
        }
    }

    @Override
    public void delete(String agentId, String userId, String sessionId) {
        delete(agentId, sessionId);
    }

    @Override
    public Set<String> listSessionIds(String agentId) {
        UUID user = parseId(agentId, "agentId");
        return sessions.listSessionIds(user).stream().map(UUID::toString).collect(java.util.stream.Collectors.toSet());
    }

    private SessionState requireSession(String agentId, String userId, String sessionId) {
        UUID agent = parseId(agentId, "agentId");
        UUID user = parseId(userId, "userId");
        UUID session = parseId(sessionId, "sessionId");
        if (!agent.equals(user)) {
            throw new IllegalArgumentException("agentId and userId must identify the same user");
        }
        SessionState value = sessions.findBySessionIdAndUserId(session, user)
                .orElseThrow(() -> new IllegalStateException("STATE_SESSION_NOT_BOUND"));
        if (!harnessVersion.equals(value.harnessVersion())) {
            throw new IllegalStateException("STATE_RUNTIME_VERSION_STALE");
        }
        return value;
    }

    private static UUID parseId(String value, String field) {
        UUID parsed = UuidGenerator.parse(value);
        if (parsed == null) {
            throw new IllegalArgumentException(field + " must be a UUID");
        }
        return parsed;
    }
}
