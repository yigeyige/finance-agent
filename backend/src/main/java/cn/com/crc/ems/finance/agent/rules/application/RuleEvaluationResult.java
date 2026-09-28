package cn.com.crc.ems.finance.agent.rules.application;

import cn.com.crc.ems.finance.agent.evidence.domain.ComputationWitness;
import cn.com.crc.ems.finance.agent.evidence.domain.Finding;

/** 规则执行产出的判断和计算见证。 */
public record RuleEvaluationResult(Finding finding, ComputationWitness witness) {

    public RuleEvaluationResult {
        if (finding == null || witness == null) {
            throw new IllegalArgumentException("rule evaluation result is invalid");
        }
    }
}
