package cn.com.crc.ems.finance.agent.rules.application;

/** 版本化 Java 确定性规则 SPI。 */
public interface DeterministicRule {

    String ruleSetVersion();

    String algorithmVersion();

    RuleFindingDraft evaluate(RuleInput input);
}
