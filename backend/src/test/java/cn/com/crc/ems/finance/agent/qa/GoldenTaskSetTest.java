package cn.com.crc.ems.finance.agent.qa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.source.application.SourceQuery;
import cn.com.crc.ems.finance.agent.source.application.SyntheticScenario;
import cn.com.crc.ems.finance.agent.source.infrastructure.SyntheticConfigSourceAdapter;

/** 固化合成黄金任务集的版本、schema、确定性和敏感字段边界。 */
class GoldenTaskSetTest {

    private static final JsonMapper JSON = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS).build();
    private static final Set<String> PACKET_SECTIONS = Set.of("problem", "premises", "scope", "sources", "facts",
            "computations", "professionalJudgments", "recommendations", "businessConfirmations",
            "validationPreparation", "handoff");

    @Test
    void fixtureMatchesSchemaAndCoversAllSyntheticScenarios() throws IOException {
        JsonNode schema = read("golden/task-fixture.schema.json");
        JsonNode fixture = read("golden/task-fixture.golden-task-set-v1.json");
        assertRequiredProperties(fixture, schema);
        assertThat(fixture.path("fixtureVersion").asText()).isEqualTo("golden-task-set-v1");
        assertThat(fixture.path("seed").asInt()).isEqualTo(17);
        assertThat(fixture.path("sourceVersion").asText()).isEqualTo("synthetic-config-v1");
        assertThat(fixture.path("ruleSetVersion").asText()).isEqualTo("rules.personnel-reference.v1");
        assertThat(fixture.path("packetSchemaVersion").asText()).isEqualTo("packet-v1");

        Set<String> scenarios = new HashSet<>();
        SyntheticConfigSourceAdapter adapter = new SyntheticConfigSourceAdapter();
        for (JsonNode scenario : fixture.path("scenarios")) {
            assertScenarioShape(scenario, schema.path("properties").path("scenarios").path("items"));
            assertThat(scenarios.add(scenario.path("sourceScenario").asText())).isTrue();
            SyntheticScenario sourceScenario = SyntheticScenario.valueOf(scenario.path("sourceScenario").asText());
            var query = new SourceQuery("config.employee.references.v1", Map.of("page", "1", "size", "2"));
            var first = adapter.query(query, sourceScenario, fixture.path("seed").asLong());
            assertThat(first).isEqualTo(adapter.query(query, sourceScenario, fixture.path("seed").asLong()));
            assertThat(toSet(scenario.path("expectedPacketSections"))).containsExactlyInAnyOrderElementsOf(PACKET_SECTIONS);
            if (sourceScenario == SyntheticScenario.PAGINATION_FAILURE) {
                assertThat(first.hasNext()).isTrue();
                assertThatThrownBy(() -> adapter.query(
                        new SourceQuery("config.employee.references.v1", Map.of("page", "2", "size", "2")),
                        sourceScenario, fixture.path("seed").asLong()))
                        .hasMessage("SYNTHETIC_PAGINATION_FAILURE");
            }
            if (sourceScenario == SyntheticScenario.COMPLETE_NO_MATCH) {
                assertThat(first.records()).isEmpty();
            }
        }
        assertThat(scenarios).containsExactlyInAnyOrderElementsOf(Set.of(
                "BASELINE", "COMPLETE_NO_MATCH", "PARTIAL_NO_MATCH", "PAGINATION_FAILURE",
                "CROSS_SOURCE_CONFLICT", "FUTURE_REFERENCE", "UNKNOWN_GAP", "NEW_REFERENCE"));
    }

    @Test
    void fixtureContainsNoRealOrSensitiveValues() throws IOException {
        String fixture = new String(readBytes("golden/task-fixture.golden-task-set-v1.json"),
                java.nio.charset.StandardCharsets.UTF_8).toLowerCase(java.util.Locale.ROOT);
        assertThat(fixture).doesNotContain("password", "credential", "secret", "sql", "prompt", "reasoning",
                "source_raw");
        assertThat(fixture).contains("golden-task-set-v1", "sourcescenario");
    }

    private static void assertRequiredProperties(JsonNode value, JsonNode schema) {
        for (JsonNode property : schema.path("required")) {
            assertThat(value.has(property.asText())).as("required property %s", property.asText()).isTrue();
        }
        assertThat(schema.path("additionalProperties").asBoolean()).isFalse();
    }

    private static void assertScenarioShape(JsonNode scenario, JsonNode schema) {
        assertRequiredProperties(scenario, schema);
        assertThat(scenario.path("expectedPacketSections").isArray()).isTrue();
        assertThat(schema.path("additionalProperties").asBoolean()).isFalse();
    }

    private static Set<String> toSet(JsonNode array) {
        Set<String> values = new HashSet<>();
        array.forEach(node -> values.add(node.asText()));
        return values;
    }

    private static JsonNode read(String resource) throws IOException {
        return JSON.readTree(readBytes(resource));
    }

    private static byte[] readBytes(String resource) throws IOException {
        try (InputStream stream = GoldenTaskSetTest.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IOException("missing golden fixture: " + resource);
            }
            return stream.readAllBytes();
        }
    }
}
