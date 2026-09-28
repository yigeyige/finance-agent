package cn.com.crc.ems.finance.agent.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import cn.com.crc.ems.finance.agent.evidence.domain.RecomputeStatus;
import cn.com.crc.ems.finance.agent.rules.application.ApprovedSnapshot;
import cn.com.crc.ems.finance.agent.rules.application.DeterministicRuleCatalog;
import cn.com.crc.ems.finance.agent.rules.application.PersonnelReferenceRule;
import cn.com.crc.ems.finance.agent.rules.application.RuleEvaluationContext;
import cn.com.crc.ems.finance.agent.rules.application.RuleEvaluationService;
import cn.com.crc.ems.finance.agent.rules.application.RuleInput;
import cn.com.crc.ems.finance.agent.rules.application.RuleRecomputeService;

class RuleEvaluationTest {

    private static final Instant NOW = Instant.parse("2026-09-28T00:00:00Z");

    @Test
    void goldenVectorProducesStableFindingAndWitnessAndMatchesOnRecompute() {
        RuleInput input = input("CURRENT");
        var context = new RuleEvaluationContext(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), NOW,
                UUID.randomUUID());
        var evaluation = new RuleEvaluationService().evaluate(new PersonnelReferenceRule(), context, input);

        assertThat(evaluation.finding().judgmentMode().name()).isEqualTo("DETERMINISTIC");
        assertThat(evaluation.finding().statement()).contains("CURRENT_EFFECTIVE");
        assertThat(evaluation.witness().inputDigest()).hasSize(64);
        assertThat(evaluation.witness().outputDigest()).hasSize(64);
        assertThat(new RuleRecomputeService(new DeterministicRuleCatalog(new PersonnelReferenceRule()))
                .recompute("rules.personnel-reference.v1", input, evaluation.witness()))
                .isEqualTo(RecomputeStatus.MATCHED);
    }

    @Test
    void digestChangeAndUnknownRuleVersionCannotMatch() {
        RuleInput input = input("CURRENT");
        var context = new RuleEvaluationContext(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), NOW,
                UUID.randomUUID());
        var evaluation = new RuleEvaluationService().evaluate(new PersonnelReferenceRule(), context, input);
        var recompute = new RuleRecomputeService(new DeterministicRuleCatalog(new PersonnelReferenceRule()));

        assertThat(recompute.recompute("rules.personnel-reference.v1", input("FUTURE"), evaluation.witness()))
                .isEqualTo(RecomputeStatus.MISMATCHED);
        assertThat(recompute.recompute("rules.unknown.v9", input, evaluation.witness()))
                .isEqualTo(RecomputeStatus.UNAVAILABLE);
    }

    @Test
    void missingOrUnauthorizedSnapshotsAreRejectedBeforeDeterministicConclusion() {
        assertThatThrownBy(() -> new RuleInput(List.of())).isInstanceOf(IllegalArgumentException.class);
        var payload = JsonNodeFactory.instance.objectNode().put("classification", "CURRENT").put("secret", "x");
        assertThatThrownBy(() -> new ApprovedSnapshot(UUID.randomUUID(), payload, Set.of("classification")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static RuleInput input(String classification) {
        var payload = JsonNodeFactory.instance.objectNode().put("classification", classification)
                .put("syntheticReferenceId", "REF-1");
        return new RuleInput(List.of(new ApprovedSnapshot(UUID.randomUUID(), payload,
                Set.of("classification", "syntheticReferenceId"))));
    }
}
