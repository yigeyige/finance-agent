package cn.com.crc.ems.finance.agent.qa;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

/** 校验 Harness 六项能力的验收表、生产白名单和 Sandbox 部署边界。 */
class HarnessCapabilityAcceptanceContractTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final Set<String> CAPABILITIES = Set.of("CONTEXT_COMPRESSION", "CONTROLLED_WORKSPACE",
            "CONTROLLED_SKILLS", "CONTROLLED_SUBAGENTS", "LONG_TERM_MEMORY", "DOCKER_SANDBOX");

    @Test
    void fixtureDeclaresAllCapabilitiesAndManualEvidence() throws IOException {
        JsonNode fixture = fixture();
        assertThat(fixture.path("fixtureVersion").asText()).isEqualTo("harness-capability-acceptance-v1");
        assertThat(fixture.path("capabilities")).hasSize(6);
        Set<String> ids = new java.util.HashSet<>();
        fixture.path("capabilities").forEach(capability -> {
            ids.add(capability.path("id").asText());
            assertThat(capability.path("functionalChecks")).isNotEmpty();
            assertThat(capability.path("securityChecks")).isNotEmpty();
            assertThat(capability.path("failureChecks")).isNotEmpty();
            assertThat(capability.path("manualRequired").asBoolean()).isTrue();
        });
        assertThat(ids).containsExactlyInAnyOrderElementsOf(CAPABILITIES);
        assertThat(fixture.toString()).doesNotContain("password", "credential", "docker.sock");
    }

    @Test
    void deploymentAndScriptKeepSandboxBoundary() throws IOException {
        String backend = readRepoFile("deploy/backend.Dockerfile");
        String frontend = readRepoFile("deploy/frontend.Dockerfile");
        String script = readRepoFile("scripts/qa-harness-capability-acceptance.ps1");
        assertThat(backend).contains("USER 10001:10001").doesNotContain("docker.sock");
        assertThat(frontend).contains("USER nginx").doesNotContain("docker.sock");
        assertThat(script).contains("-Execute", "networkCalls=0", "containerCalls=0", "JAR_LOCAL");
    }

    private static JsonNode fixture() throws IOException {
        return JSON.readTree(Path.of("..", "scripts", "fixtures", "harness-capability-acceptance-v1.json").toFile());
    }

    private static String readRepoFile(String relativePath) throws IOException {
        return Files.readString(Path.of("..", relativePath), StandardCharsets.UTF_8);
    }
}
