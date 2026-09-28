package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.com.crc.ems.finance.agent.harness.application.AllowedContext;
import cn.com.crc.ems.finance.agent.harness.application.CancellationToken;
import cn.com.crc.ems.finance.agent.harness.application.DeterministicModelStub;
import cn.com.crc.ems.finance.agent.harness.application.ModelBudget;
import cn.com.crc.ems.finance.agent.harness.application.ModelFixture;
import cn.com.crc.ems.finance.agent.harness.application.ModelGatewayException;
import cn.com.crc.ems.finance.agent.harness.application.ModelPurpose;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplateVersion;
import cn.com.crc.ems.finance.agent.harness.application.StepProposal;
import cn.com.crc.ems.finance.agent.harness.application.StepProposalSchema;

class ModelGatewayContractTest {

    private static final PromptTemplateVersion TEMPLATE = new PromptTemplateVersion("steps", "v1");
    private static final AllowedContext CONTEXT = new AllowedContext(
            JsonNodeFactory.instance.objectNode().put("findingType", "PERSONNEL_REFERENCE"),
            Set.of("evidence-1"), Set.of("findingType"), Map.of("taskType", "config-check"));
    private static final UUID SOURCE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SCOPE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void deterministicStubReturnsSameStructuredResultWithoutNetwork() {
        ObjectNode fixture = validFixture();
        String key = DeterministicModelStub.fixtureKey(ModelPurpose.STEP_PROPOSAL, TEMPLATE, CONTEXT);
        DeterministicModelStub stub = new DeterministicModelStub(
                Map.of(key, new ModelFixture(fixture, 12, new BigDecimal("0.02"))), "stub-binding-v1");

        var first = stub.generateStructured(ModelPurpose.STEP_PROPOSAL, TEMPLATE, CONTEXT,
                new StepProposalSchema(), new ModelBudget(1, 20, new BigDecimal("0.03"), Duration.ofSeconds(1)),
                CancellationToken.neverCancelled());
        var second = stub.generateStructured(ModelPurpose.STEP_PROPOSAL, TEMPLATE, CONTEXT,
                new StepProposalSchema(), new ModelBudget(1, 20, new BigDecimal("0.03"), Duration.ofSeconds(1)),
                CancellationToken.neverCancelled());

        assertThat(first.value()).isEqualTo(second.value());
        assertThat(first.metadata().outputDigest()).isEqualTo(second.metadata().outputDigest());
        assertThat(first.metadata().inputEvidenceIds()).containsExactly("evidence-1");
    }

    @Test
    void forbiddenContextAndMissingFixtureFailClosed() {
        ObjectNode forbidden = JsonNodeFactory.instance.objectNode().put("prompt", "do not retain");
        assertThatThrownBy(() -> new AllowedContext(forbidden, Set.of(), Set.of(), Map.of()))
                .isInstanceOf(IllegalArgumentException.class);

        DeterministicModelStub stub = new DeterministicModelStub();
        assertThatThrownBy(() -> stub.generateStructured(ModelPurpose.STEP_PROPOSAL, TEMPLATE, CONTEXT,
                new StepProposalSchema(), new ModelBudget(1, 20, BigDecimal.ONE, Duration.ofSeconds(1)),
                CancellationToken.neverCancelled()))
                .isInstanceOf(ModelGatewayException.class)
                .extracting("code").isEqualTo("FIXTURE_NOT_FOUND");
    }

    @Test
    void schemaBudgetAndCancellationAreEnforced() {
        String key = DeterministicModelStub.fixtureKey(ModelPurpose.STEP_PROPOSAL, TEMPLATE, CONTEXT);
        ObjectNode invalid = validFixture().put("extra", true);
        DeterministicModelStub stub = new DeterministicModelStub(
                Map.of(key, new ModelFixture(invalid, 12, new BigDecimal("0.02"))), "stub-binding-v1");
        assertThatThrownBy(() -> stub.generateStructured(ModelPurpose.STEP_PROPOSAL, TEMPLATE, CONTEXT,
                new StepProposalSchema(), new ModelBudget(1, 20, BigDecimal.ONE, Duration.ofSeconds(1)),
                CancellationToken.neverCancelled()))
                .isInstanceOf(ModelGatewayException.class)
                .extracting("code").isEqualTo("SCHEMA_INVALID");

        DeterministicModelStub budgetStub = new DeterministicModelStub(
                Map.of(key, new ModelFixture(validFixture(), 12, new BigDecimal("0.02"))), "stub-binding-v1");
        assertThatThrownBy(() -> budgetStub.generateStructured(ModelPurpose.STEP_PROPOSAL, TEMPLATE, CONTEXT,
                new StepProposalSchema(), new ModelBudget(1, 10, BigDecimal.ONE, Duration.ofSeconds(1)),
                CancellationToken.neverCancelled()))
                .isInstanceOf(ModelGatewayException.class)
                .extracting("code").isEqualTo("BUDGET_EXHAUSTED");
        assertThatThrownBy(() -> budgetStub.generateStructured(ModelPurpose.STEP_PROPOSAL, TEMPLATE, CONTEXT,
                new StepProposalSchema(), new ModelBudget(1, 20, BigDecimal.ONE, Duration.ofSeconds(1)),
                () -> true))
                .isInstanceOf(ModelGatewayException.class)
                .extracting("code").isEqualTo("CANCELLED");
    }

    private static ObjectNode validFixture() {
        ObjectNode action = JsonNodeFactory.instance.objectNode()
                .put("goal", "读取配置引用")
                .put("toolCode", "read-config")
                .put("sourceBindingId", SOURCE_ID.toString())
                .put("scopeRevisionId", SCOPE_ID.toString())
                .put("queryTemplateVersion", "config-v1")
                .put("maxCalls", 1);
        action.putArray("requestedLogicalFields").add("owner").add("status");
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        root.set("recommended", action);
        root.putArray("alternatives");
        root.put("reason", "fixture reason");
        root.put("stopAssessment", "CONTINUE");
        return root;
    }
}
