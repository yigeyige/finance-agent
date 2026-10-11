package cn.com.crc.ems.finance.agent.rules.application;

import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;

/**
 *
 * @Description: 确定性规则输出的 Finding 草稿。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record RuleFindingDraft(String findingType, String statement, EffectiveClass effectiveClass) {

    public RuleFindingDraft {
        if (findingType == null || findingType.isBlank() || statement == null || statement.isBlank()
                || effectiveClass == null) {
            throw new IllegalArgumentException("rule finding draft is invalid");
        }
    }
}
