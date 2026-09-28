package cn.com.crc.ems.finance.agent.rules.application;

import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;

/** 首个人员引用确定性规则，只读取获准快照中的分类字段。 */
@Component
public class PersonnelReferenceRule implements DeterministicRule {

    @Override
    public String ruleSetVersion() {
        return "rules.personnel-reference.v1";
    }

    @Override
    public String algorithmVersion() {
        return "algorithm.java17-personnel-reference.v1";
    }

    @Override
    public RuleFindingDraft evaluate(RuleInput input) {
        boolean current = input.snapshots().stream().anyMatch(snapshot -> "CURRENT".equals(
                snapshot.retainedPayload().path("classification").asText()));
        boolean future = input.snapshots().stream().anyMatch(snapshot -> "FUTURE".equals(
                snapshot.retainedPayload().path("classification").asText()));
        EffectiveClass effectiveClass = current ? EffectiveClass.CURRENT_EFFECTIVE
                : future ? EffectiveClass.FUTURE_SCHEDULED : EffectiveClass.UNDETERMINED;
        return new RuleFindingDraft("PERSONNEL_REFERENCE",
                "人员引用分类=" + effectiveClass.name(), effectiveClass);
    }
}
