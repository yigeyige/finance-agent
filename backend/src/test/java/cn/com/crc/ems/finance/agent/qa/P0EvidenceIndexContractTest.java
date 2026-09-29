package cn.com.crc.ems.finance.agent.qa;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

/** 校验 P0 证据索引、零容忍指标和未签署条件性结论契约。 */
class P0EvidenceIndexContractTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    @Test
    void indexCoversAllP0TasksAndExistingEvidence() throws IOException {
        JsonNode fixture = fixture();
        assertThat(fixture.path("fixtureVersion").asText()).isEqualTo("p0-evidence-index-v1");
        assertThat(fixture.path("tasks")).hasSize(10);
        IntStream.rangeClosed(1, 10).forEach(index -> assertThat(fixture.path("tasks").get(index - 1).path("id").asText())
                .isEqualTo(String.format("DEV-QA-%03d", index)));
        for (JsonNode task : fixture.path("tasks")) {
            assertThat(task.path("status").asText()).isIn("VERIFIED_STATIC", "PENDING_USER");
            for (JsonNode artifact : task.path("artifacts")) {
                assertThat(Files.exists(Path.of("..", artifact.asText()))).as(artifact.asText()).isTrue();
            }
        }
    }

    @Test
    void zeroToleranceAndSignOffRemainConditional() throws IOException {
        JsonNode fixture = fixture();
        assertThat(fixture.path("zeroTolerance")).hasSize(3);
        for (JsonNode metric : fixture.path("zeroTolerance")) {
            assertThat(metric.path("value").asInt()).isZero();
            assertThat(metric.path("status").asText()).isEqualTo("PENDING_USER");
        }
        assertThat(fixture.path("signOff").path("status").asText()).isEqualTo("PENDING_USER");
        assertThat(fixture.path("signOff").path("conclusion").asText()).isEqualTo("CONDITIONAL");
        assertThat(fixture.path("signOff").path("signedBy").asText()).isBlank();
        assertThat(fixture.toString()).doesNotContain("realPayload", "apiKeyValue");
    }

    @Test
    void scriptAndGuideDeclareOfflineCheckAndUserSignOff() throws IOException {
        String script = readRepoFile("scripts/qa-p0-evidence-index.ps1");
        String guide = readRepoFile("docs/p0-evidence-gate.md");
        assertThat(script).contains("networkCalls=0", "databaseCalls=0", "PENDING_USER", "CONDITIONAL");
        assertThat(guide).contains("P0 证据包", "签署记录模板", "CONDITIONAL", "DeepSeek");
    }

    private static JsonNode fixture() throws IOException {
        return JSON.readTree(Path.of("..", "scripts", "fixtures", "p0-evidence-index-v1.json").toFile());
    }

    private static String readRepoFile(String relativePath) throws IOException {
        return Files.readString(Path.of("..", relativePath), StandardCharsets.UTF_8);
    }
}
