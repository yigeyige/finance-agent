package cn.com.crc.ems.finance.agent.rules.application;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

/**
 *
 * @Description: 仅允许已登记版本进入确定性规则执行和复算。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Component
public class DeterministicRuleCatalog {

    private final Map<String, DeterministicRule> rules;

    public DeterministicRuleCatalog(PersonnelReferenceRule personnelReferenceRule) {
        this.rules = Map.of(personnelReferenceRule.ruleSetVersion(), personnelReferenceRule);
    }

    public Optional<DeterministicRule> find(String ruleSetVersion) {
        return Optional.ofNullable(rules.get(ruleSetVersion));
    }
}
