package cn.com.crc.ems.finance.agent.qa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;
import cn.com.crc.ems.finance.agent.packet.application.ScopeRecheckRepository;
import cn.com.crc.ems.finance.agent.packet.application.ScopeRecheckService;
import cn.com.crc.ems.finance.agent.packet.application.ScopeSnapshot;
import cn.com.crc.ems.finance.agent.packet.application.RecheckDecision;

/**
 *
 * @Description: 校验核心合成纵向切片的固定输入、重查边界和验收资产。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 00:00:00
 */
class CoreSyntheticVerticalSliceContractTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final UUID TASK_ID = UUID.fromString("00000000-0000-4000-8000-000000000101");
    private static final UUID SCOPE_ID = UUID.fromString("00000000-0000-4000-8000-000000000102");
    private static final UUID ACTOR_ID = UUID.fromString("00000000-0000-4000-8000-000000000001");
    private static final Pattern FORBIDDEN = Pattern.compile("password|api[_-]?key|prompt|reasoning|real[_-]?data",
            Pattern.CASE_INSENSITIVE);

    @Test
    void fixtureIsVersionedSyntheticAndCoversTheWholeSlice() throws IOException {
        JsonNode fixture = fixture();
        assertThat(fixture.path("fixtureVersion").asText()).isEqualTo("core-synthetic-vertical-slice-v1");
        assertThat(fixture.path("seed").asInt()).isEqualTo(17);
        assertThat(fixture.path("operatorIdentity").asText()).isEqualTo("operator");
        assertThat(fixture.path("viewerIdentity").asText()).isEqualTo("viewer");
        assertThat(fixture.path("steps")).hasSize(3);
        assertThat(fixture.path("steps").findValuesAsText("recommended")).containsExactly("true", "false", "false");

        Set<String> required = new HashSet<>();
        fixture.path("requiredArtifacts").forEach(value -> required.add(value.asText()));
        assertThat(required).contains("task", "step_approval", "fact_snapshot", "coverage_record", "finding",
                "computation_witness", "professional_confirmation", "packet_review", "scope_recheck",
                "packet_revision", "audit_event", "handoff");
        assertThat(FORBIDDEN.matcher(fixture.toString()).find()).isFalse();
    }

    @Test
    void recheckFixtureBlocksAddedReferenceThenAllowsStablePublication() throws IOException {
        JsonNode recheck = fixture().path("recheck");
        ScopeRecheckRepository repository = mock(ScopeRecheckRepository.class);
        ScopeRecheckService service = new ScopeRecheckService(repository);

        var stale = service.recheck(TASK_ID, SCOPE_ID, snapshots(recheck.path("baseline")),
                snapshots(recheck.path("changedCurrent")), Instant.parse("2026-09-29T00:00:00Z"), ACTOR_ID);
        assertThat(stale.decision()).isEqualTo(RecheckDecision.STALE_DRAFT);
        assertThat(stale.diffs()).anyMatch(diff -> diff.objectKey().equals("workflow:WF-SYN-NEW-001"));

        var clean = service.recheck(TASK_ID, SCOPE_ID, snapshots(recheck.path("baseline")),
                snapshots(recheck.path("stableCurrent")), Instant.parse("2026-09-29T00:01:00Z"), ACTOR_ID);
        assertThat(clean.decision()).isEqualTo(RecheckDecision.READY_TO_PUBLISH);
        assertThat(clean.diffs()).isEmpty();
        verify(repository, times(2)).insert(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void runbookAndEvidenceQueryDeclareSafeExecutionBoundary() throws IOException {
        String script = readRepoFile("scripts/qa-core-synthetic-vertical-slice.ps1");
        String query = readRepoFile("scripts/qa-core-synthetic-vertical-slice.sql");
        assertThat(script).contains("-Execute", "core-synthetic-vertical-slice-v1", "X-Test-Identity",
                "Invoke-WebRequest");
        assertThat(query).contains("task.investigation_task", "task.step_approval", "evidence.fact_snapshot",
                "evidence.coverage_record", "evidence.computation_witness", "packet.packet_review",
                "packet.scope_recheck", "packet.packet_revision", "governance.audit_event", "packet.handoff");
        assertThat(script.toLowerCase()).doesNotContain("password =", "api_key =", "deepseek_api_key =");
    }

    private static JsonNode fixture() throws IOException {
        return JSON.readTree(Path.of("..", "scripts", "fixtures", "core-synthetic-vertical-slice-v1.json").toFile());
    }

    private static String readRepoFile(String relativePath) throws IOException {
        return Files.readString(Path.of("..", relativePath), StandardCharsets.UTF_8);
    }

    private static List<ScopeSnapshot> snapshots(JsonNode values) {
        var result = new java.util.ArrayList<ScopeSnapshot>();
        values.forEach(value -> result.add(new ScopeSnapshot(value.path("objectKey").asText(),
                value.path("fieldsDigest").asText(), EffectiveClass.valueOf(value.path("effectiveClass").asText()),
                value.path("sourceVersion").asText(), value.path("ruleSetVersion").asText(),
                value.path("templateVersion").asText())));
        return result;
    }
}
