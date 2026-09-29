package cn.com.crc.ems.finance.agent.packet;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.evidence.domain.CoverageCompleteness;
import cn.com.crc.ems.finance.agent.packet.application.PacketWordingValidator;

/** 验证覆盖完整性、事实来源和业务影响措辞不会越过决策包门禁。 */
class PacketWordingValidatorTest {

    private final JsonMapper json = JsonMapper.builder().build();
    private final PacketWordingValidator validator = new PacketWordingValidator();

    @Test
    void completeSourceFactsCanPassWhenImpactIsConfirmed() throws Exception {
        assertThatCode(() -> validator.validate(json.readTree("""
                {"facts":{"current":[{"sourceKind":"SOURCE"}],"undetermined":[]},
                 "recommendations":["合成样例建议"],"businessConfirmations":{"pendingItems":[]}}
                """), CoverageCompleteness.COMPLETE, true)).doesNotThrowAnyException();
    }

    @Test
    void partialCoverageRequiresLimitationsAndRejectsAbsoluteClaim() throws Exception {
        var document = json.readTree("""
                {"limitations":"仅覆盖合成页","facts":{"current":[],"undetermined":[{"reason":"分页失败"}]},
                 "recommendations":["本次覆盖全部对象"]}
                """);
        assertThatThrownBy(() -> validator.validate(document, CoverageCompleteness.PARTIAL, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("incomplete coverage cannot claim full scope");
        assertThatThrownBy(() -> validator.validate(json.readTree("""
                {"facts":{"current":[],"undetermined":[]}}
                """), CoverageCompleteness.PARTIAL, true))
                .hasMessage("incomplete coverage requires limitations");
    }

    @Test
    void unknownCoverageRequiresUndeterminedFacts() throws Exception {
        assertThatThrownBy(() -> validator.validate(json.readTree("""
                {"limitations":"来源水位变化，无法确认","facts":{"current":[],"undetermined":[]}}
                """), CoverageCompleteness.UNKNOWN, true))
                .hasMessage("unknown coverage requires undetermined facts");
    }

    @Test
    void ragMemoryAndModelHintsCannotBecomeCurrentFacts() throws Exception {
        for (String source : new String[] {"RAG", "MEMORY", "MODEL"}) {
            var document = json.readTree("{\"facts\":{\"current\":[{\"sourceKind\":\"" + source
                    + "\"}],\"undetermined\":[]}}");
            assertThatThrownBy(() -> validator.validate(document, CoverageCompleteness.COMPLETE, true))
                    .hasMessage("RAG, memory or model content cannot be a current fact");
        }
    }

    @Test
    void unconfirmedImpactRequiresPendingItemsAndQualifiedWording() throws Exception {
        var invalid = json.readTree("""
                {"facts":{"current":[],"undetermined":[]},"businessConfirmations":{"pendingItems":["待确认"]},
                 "recommendations":["已造成影响"]}
                """);
        assertThatThrownBy(() -> validator.validate(invalid, CoverageCompleteness.COMPLETE, false))
                .hasMessage("unconfirmed business impact must remain qualified");
        var valid = json.readTree("""
                {"facts":{"current":[],"undetermined":[]},"businessConfirmations":{"pendingItems":["待确认"]},
                 "recommendations":["需人工确认业务影响"]}
                """);
        assertThatCode(() -> validator.validate(valid, CoverageCompleteness.COMPLETE, false))
                .doesNotThrowAnyException();
    }
}
