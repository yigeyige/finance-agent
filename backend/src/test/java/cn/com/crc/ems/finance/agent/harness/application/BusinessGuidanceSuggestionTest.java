package cn.com.crc.ems.finance.agent.harness.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/** 验证初稿继续标志、可选附件和字段结构兼容。 */
class BusinessGuidanceSuggestionTest {

    private static final String DRAFT = """
            {
              "reply": "已生成初稿",
              "draft": {
                "subjectKey": "单据起草",
                "objectKey": "报账单",
                "recommendedGoal": "提交审批",
                "alternativeGoals": [],
                "reason": "依据用户说明",
                "conclusion": "流程结构已生成",
                "evidence": [],
                "needMoreEvidence": true,
                "needTableReselection": false
              }
            }
            """;

    @Test
    void readsNestedFlagsAndAllowsMissingAttachments() {
        BusinessGuidanceSuggestion suggestion = BusinessGuidanceSuggestion.fromJson(
                CanonicalJsonCodec.parse(DRAFT));

        assertThat(suggestion.requestedAttachments()).isEmpty();
        assertThat(suggestion.needMoreEvidence()).isTrue();
        assertThat(suggestion.needTableReselection()).isFalse();
    }

    @Test
    void rejectsUnknownDraftField() {
        String json = """
                {"reply":"ok","draft":{"subjectKey":"a","objectKey":"b","recommendedGoal":"c",
                "alternativeGoals":[],"reason":"d","conclusion":"e","evidence":[],"unknownField":"x"}}
                """;

        assertThatThrownBy(() -> BusinessGuidanceSuggestion.fromJson(CanonicalJsonCodec.parse(json)))
                .isInstanceOf(BusinessModelOutputException.class);
    }
}
