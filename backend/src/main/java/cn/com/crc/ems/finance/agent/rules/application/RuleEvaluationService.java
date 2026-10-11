package cn.com.crc.ems.finance.agent.rules.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.evidence.domain.ComputationWitness;
import cn.com.crc.ems.finance.agent.evidence.domain.Finding;
import cn.com.crc.ems.finance.agent.evidence.domain.FindingStatus;
import cn.com.crc.ems.finance.agent.evidence.domain.JudgmentMode;
import cn.com.crc.ems.finance.agent.evidence.domain.RecomputeStatus;

/**
 *
 * @Description: 编排获准快照、版本化规则、Finding 和计算见证。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Service
public class RuleEvaluationService {

    private final RuleInputNormalizer normalizer;

    public RuleEvaluationService() {
        this(new RuleInputNormalizer());
    }

    public RuleEvaluationService(RuleInputNormalizer normalizer) {
        this.normalizer = normalizer;
    }

    /**
     * 
     * @description: 执行确定性规则并创建新的 Finding 与 ComputationWitness
     *
     * @param: rule 已登记的确定性规则
     * @param: context Finding 业务上下文
     * @param: input 获准快照输入
     * @return: 规则判断及其计算见证
     * @author: yigeyige99
     */
    public RuleEvaluationResult evaluate(DeterministicRule rule, RuleEvaluationContext context, RuleInput input) {
        if (rule == null || context == null || input == null) {
            throw new IllegalArgumentException("rule, context and input are required");
        }
        NormalizedRuleInput normalized = normalizer.normalize(input);
        RuleFindingDraft draft = rule.evaluate(input);
        String outputDigest = normalizer.outputDigest(draft);
        Finding finding = new Finding(context.findingId(), context.taskId(), draft.findingType(), draft.statement(),
                JudgmentMode.DETERMINISTIC, draft.effectiveClass(), context.scopeRevisionId(), FindingStatus.DRAFT,
                context.actorId(), context.checkedAt(), context.actorId(), context.checkedAt());
        ComputationWitness witness = new ComputationWitness(UUID.randomUUID(), context.findingId(),
                rule.algorithmVersion(), rule.ruleSetVersion(), normalized.inputDigest(), outputDigest,
                normalized.snapshotIds(), context.checkedAt(), RecomputeStatus.NOT_RUN, null, context.actorId(),
                context.checkedAt(), context.actorId(), context.checkedAt());
        return new RuleEvaluationResult(finding, witness);
    }
}
