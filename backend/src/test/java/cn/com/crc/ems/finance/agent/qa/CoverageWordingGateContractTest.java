package cn.com.crc.ems.finance.agent.qa;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

/** 校验覆盖度与事实措辞门禁 fixture、脚本和领域验证器契约。 */
class CoverageWordingGateContractTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    @Test
    void fixtureCoversCompletenessSourcesAndImpactConfirmation() throws IOException {
        JsonNode fixture = fixture();
        assertThat(fixture.path("fixtureVersion").asText()).isEqualTo("coverage-wording-gate-v1");
        assertThat(fixture.path("scenarios")).hasSize(10);
        assertThat(fixture.path("nonAuthoritativeSources")).extracting(JsonNode::asText)
                .containsExactlyInAnyOrder("RAG", "MEMORY", "MODEL");
        assertThat(fixture.toString()).doesNotContain("password", "credential", "realPayload");
    }

    @Test
    void scriptIsOfflineFirstAndValidatorContainsAllRules() throws IOException {
        String script = readRepoFile("scripts/qa-coverage-wording-gate.ps1");
        String validator = readRepoFile("backend/src/main/java/cn/com/crc/ems/finance/agent/packet/application/PacketWordingValidator.java");
        assertThat(script).contains("-Execute", "networkCalls=0", "databaseCalls=0", "ResultDirectory is required");
        assertThat(validator).contains("CoverageCompleteness.COMPLETE", "CoverageCompleteness.UNKNOWN",
                "NON_AUTHORITATIVE_SOURCES", "DETERMINISTIC_IMPACT");
    }

    private static JsonNode fixture() throws IOException {
        return JSON.readTree(Path.of("..", "scripts", "fixtures", "coverage-wording-gate-v1.json").toFile());
    }

    private static String readRepoFile(String relativePath) throws IOException {
        return Files.readString(Path.of("..", relativePath), StandardCharsets.UTF_8);
    }
}
