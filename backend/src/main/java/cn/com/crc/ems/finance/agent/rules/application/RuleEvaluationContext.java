package cn.com.crc.ems.finance.agent.rules.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 确定性规则生成 Finding 和见证所需的业务上下文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record RuleEvaluationContext(UUID findingId, UUID taskId, UUID scopeRevisionId,
        Instant checkedAt, UUID actorId) {

    public RuleEvaluationContext {
        if (findingId == null || taskId == null || scopeRevisionId == null || checkedAt == null) {
            throw new IllegalArgumentException("rule evaluation context is invalid");
        }
    }
}
