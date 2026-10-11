package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.crc.ems.finance.agent.harness.application.AgentStateCodec;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import io.agentscope.core.state.State;

/**
 *
 * @Description: 复用 Jackson 序列化 AgentScope 状态，并在写入前执行敏感字段和摘要校验。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Component
public final class JacksonAgentStateCodec implements AgentStateCodec {

    public static final String SCHEMA_VERSION = "agentscope-state-v1";
    private final ObjectMapper objectMapper;

    public JacksonAgentStateCodec() {
        this(new ObjectMapper());
    }

    public JacksonAgentStateCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public StateReference encode(State state) {
        if (state == null) {
            throw new IllegalArgumentException("state is required");
        }
        try {
            String json = objectMapper.writeValueAsString(state);
            return new StateReference(SCHEMA_VERSION, json, Sha256Digest.hex(json));
        } catch (IOException exception) {
            throw new IllegalArgumentException("state cannot be serialized", exception);
        }
    }

    @Override
    public <T extends State> T decode(StateReference stateReference, Class<T> stateType) {
        if (stateReference == null || stateType == null) {
            throw new IllegalArgumentException("stateReference and stateType are required");
        }
        if (!SCHEMA_VERSION.equals(stateReference.schemaVersion())
                || !Sha256Digest.hex(stateReference.json()).equalsIgnoreCase(stateReference.digest())) {
            throw new IllegalStateException("state schema or digest is invalid");
        }
        try {
            return objectMapper.readValue(stateReference.json(), stateType);
        } catch (IOException exception) {
            throw new IllegalStateException("state cannot be restored", exception);
        }
    }
}
