package cn.com.crc.ems.finance.agent.packet.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

/** 验证决策包嵌套 JSON 可由 Spring Boot 4 的 Jackson 3 HTTP 类型绑定。 */
class PacketDraftRequestJsonTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void acceptsNestedPacketSections() throws Exception {
        PacketDraftRequest request = jsonMapper.readValue("""
                {
                  "scopeRevisionId":"00000000-0000-4000-8000-000000000020",
                  "runtimeBundleId":"00000000-0000-4000-8000-000000000010",
                  "problem":"合成问题",
                  "premises":[],"scope":{},"sources":[],"facts":{},"computations":{},
                  "professionalJudgments":[],"recommendations":[],"businessConfirmations":{},
                  "validationPreparation":{},"handoff":{}
                }
                """, PacketDraftRequest.class);

        assertThat(request.premises().isArray()).isTrue();
        assertThat(request.handoff().isObject()).isTrue();
    }
}
