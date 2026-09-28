package cn.com.crc.ems.finance.agent.tool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.tool.application.FieldAccessPolicy;
import cn.com.crc.ems.finance.agent.tool.application.TrustedFieldProjector;

class TrustedFieldProjectorTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void projectsReadRetainAndModelWithoutUnretainedField() throws Exception {
        var policy = new FieldAccessPolicy(Set.of("id", "amount", "temporary"), Set.of("id", "amount"), Set.of("id"));
        var projection = new TrustedFieldProjector().project(mapper.readTree("{\"id\":1,\"amount\":2,\"temporary\":3}"), policy);

        assertThat(projection.readPayload().get("temporary")).isNotNull();
        assertThat(projection.retainedPayload().get("temporary")).isNull();
        assertThat(projection.modelPayload().get("amount")).isNull();
    }

    @Test
    void rejectsUnauthorizedAndSensitiveSourceFields() throws Exception {
        var policy = new FieldAccessPolicy(Set.of("id"), Set.of("id"), Set.of("id"));
        TrustedFieldProjector projector = new TrustedFieldProjector();
        assertThatThrownBy(() -> projector.project(mapper.readTree("{\"id\":1,\"secret\":\"x\"}"), policy))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> projector.project(mapper.readTree("{\"id\":1,\"other\":2}"), policy))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
