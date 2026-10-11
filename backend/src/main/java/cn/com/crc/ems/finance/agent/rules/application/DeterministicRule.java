package cn.com.crc.ems.finance.agent.rules.application;

/**
 *
 * @Description: 版本化 Java 确定性规则 SPI。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface DeterministicRule {

    String ruleSetVersion();

    String algorithmVersion();

    RuleFindingDraft evaluate(RuleInput input);
}
