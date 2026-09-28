package cn.com.crc.ems.finance.agent.rules.application;

import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;

/** 确定性规则输出的 Finding 草稿。 */
public record RuleFindingDraft(String findingType, String statement, EffectiveClass effectiveClass) {

    public RuleFindingDraft {
        if (findingType == null || findingType.isBlank() || statement == null || statement.isBlank()
                || effectiveClass == null) {
            throw new IllegalArgumentException("rule finding draft is invalid");
        }
    }
}
