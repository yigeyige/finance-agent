package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.infrastructure.JacksonHarnessStateSerializer;

class HarnessStateSerializerTest {

    @Test
    void serializerRoundTripsSafeReference() {
        JacksonHarnessStateSerializer serializer = new JacksonHarnessStateSerializer();
        var state = serializer.serialize("harness-v1", "{\"checkpoint\":\"cp-1\"}", "b".repeat(64));

        assertThat(serializer.deserialize(state)).isEqualTo(state.json());
        assertThat(state.schemaVersion()).isEqualTo("harness-v1");
        assertThat(state.digest()).isEqualTo("b".repeat(64));
    }

    @Test
    void serializerRejectsCredentialAndRawResponseKeysRecursively() {
        JacksonHarnessStateSerializer serializer = new JacksonHarnessStateSerializer();

        assertThatThrownBy(() -> serializer.serialize("harness-v1", "{\"nested\":{\"access_token\":\"x\"}}",
                "c".repeat(64))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> serializer.serialize("harness-v1", "{\"source_raw_response\":{}}",
                "c".repeat(64))).isInstanceOf(IllegalArgumentException.class);
    }
}
