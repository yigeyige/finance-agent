package cn.com.crc.ems.finance.agent.qa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.harness.application.ContextEvidence;
import cn.com.crc.ems.finance.agent.harness.application.ThinkingContentFilter;
import cn.com.crc.ems.finance.agent.source.application.ConfigSourceAdapter;
import cn.com.crc.ems.finance.agent.tool.application.FieldAccessPolicy;
import cn.com.crc.ems.finance.agent.tool.application.SensitiveFieldScanner;
import cn.com.crc.ems.finance.agent.tool.application.TrustedFieldProjector;

/**
 *
 * @Description: 校验数据边界负向 fixture、可信投影和只读源适配器契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 00:00:00
 */
class DataBoundaryNegativeGateContractTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final UUID TASK_ID = UUID.fromString("00000000-0000-4000-8000-000000000201");

    @Test
    void fixtureDeclaresSevenTargetsAndZeroExpectedLeaks() throws IOException {
        JsonNode fixture = fixture();
        assertThat(fixture.path("fixtureVersion").asText()).isEqualTo("data-boundary-negative-v1");
        assertThat(fixture.path("scanTargets")).hasSize(7);
        assertThat(fixture.path("expected").path("forbiddenPersistence").asInt()).isZero();
        assertThat(fixture.path("expected").path("forbiddenModelTransmission").asInt()).isZero();
        assertThat(fixture.path("expected").path("sourceWrites").asInt()).isZero();
        assertThat(fixture.toString()).doesNotContain("payload");
    }

    @Test
    void readButNotRetainedAndNotModelFieldStaysInTrustedMemoryOnly() throws IOException {
        JsonNode source = JSON.readTree("{\"syntheticEmployeeId\":\"EMP-SYN-001\",\"classification\":\"CURRENT\",\"displayName\":\"NEGATIVE_BOUNDARY_MARKER_20260929\"}");
        var policy = new FieldAccessPolicy(Set.of("syntheticEmployeeId", "classification", "displayName"),
                Set.of("syntheticEmployeeId", "classification"), Set.of("syntheticEmployeeId", "classification"));
        var projection = new TrustedFieldProjector().project(source, policy);
        assertThat(projection.readPayload().has("displayName")).isTrue();
        assertThat(projection.retainedPayload().has("displayName")).isFalse();
        assertThat(projection.modelPayload().has("displayName")).isFalse();
        assertThat(new ContextEvidence("evidence-201", TASK_ID, projection.modelPayload(),
                Set.of("syntheticEmployeeId", "classification")).projectedPayload().has("displayName")).isFalse();
    }

    @Test
    void forbiddenReadAndReasoningFieldsAreRejectedBeforeBoundaryCrossing() throws IOException {
        assertThatThrownBy(() -> SensitiveFieldScanner.requireSafe(JSON.readTree("{\"credential\":\"x\"}")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(ThinkingContentFilter.filter(JSON.readTree("{\"safe\":1,\"reasoning_content\":\"x\"}"))
                .has("reasoning_content")).isFalse();
    }

    @Test
    void sourceAdapterHasNoWriteOperation() {
        assertThat(ConfigSourceAdapter.class.getMethods()).allMatch(this::isReadMethod);
    }

    @Test
    void scanAssetsDeclareReadOnlyExecutionBoundary() throws IOException {
        String script = readRepoFile("scripts/qa-data-boundary-negative-gate.ps1");
        String query = readRepoFile("scripts/qa-data-boundary-negative-gate.sql");
        assertThat(script).contains("-Execute", "networkCalls=0", "databaseCalls=0", "sourceWrites=0");
        assertThat(query).contains("WITH target_values", "source_writes", "count(*)").doesNotContain("INSERT", "UPDATE", "DELETE");
    }

    private boolean isReadMethod(Method method) {
        String name = method.getName().toLowerCase();
        return name.equals("catalog") || name.equals("query");
    }

    private static JsonNode fixture() throws IOException {
        return JSON.readTree(Path.of("..", "scripts", "fixtures", "data-boundary-negative-v1.json").toFile());
    }

    private static String readRepoFile(String relativePath) throws IOException {
        return Files.readString(Path.of("..", relativePath), StandardCharsets.UTF_8);
    }
}
