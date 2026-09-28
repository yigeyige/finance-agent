package cn.com.crc.ems.finance.agent.rules.application;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.evidence.domain.ComputationWitness;
import cn.com.crc.ems.finance.agent.evidence.domain.RecomputeStatus;

/** 独立 rules.recompute 服务，不修改历史见证，只返回复算状态。 */
@Service
public class RuleRecomputeService {

    private final DeterministicRuleCatalog catalog;
    private final RuleInputNormalizer normalizer;

    public RuleRecomputeService(DeterministicRuleCatalog catalog) {
        this.catalog = catalog;
        this.normalizer = new RuleInputNormalizer();
    }

    /**
     * 
     * @description: 按规则版本独立复算并比较输入、输出、算法和快照摘要
     *
     * @param: ruleSetVersion 要复算的规则版本
     * @param: input 当前获准快照输入
     * @param: witness 历史计算见证
     * @return: MATCHED、MISMATCHED 或 UNAVAILABLE
     * @author: yigeyige99
     */
    public RecomputeStatus recompute(String ruleSetVersion, RuleInput input, ComputationWitness witness) {
        if (ruleSetVersion == null || input == null || witness == null) {
            return RecomputeStatus.UNAVAILABLE;
        }
        try {
            DeterministicRule rule = catalog.find(ruleSetVersion).orElse(null);
            if (rule == null) {
                return RecomputeStatus.UNAVAILABLE;
            }
            NormalizedRuleInput normalized = normalizer.normalize(input);
            RuleFindingDraft draft = rule.evaluate(input);
            String outputDigest = normalizer.outputDigest(draft);
            boolean matched = ruleSetVersion.equals(witness.ruleSetVersion())
                    && rule.algorithmVersion().equals(witness.algorithmVersion())
                    && normalized.inputDigest().equals(witness.inputDigest())
                    && outputDigest.equals(witness.outputDigest())
                    && normalized.snapshotIds().equals(witness.snapshotIds());
            return matched ? RecomputeStatus.MATCHED : RecomputeStatus.MISMATCHED;
        } catch (RuntimeException exception) {
            return RecomputeStatus.UNAVAILABLE;
        }
    }
}
