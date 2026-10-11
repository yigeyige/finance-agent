package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;

/**
 *
 * @Description: 严格 schema 校验后的下一步结构化提议。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record StepProposal(StepAction recommended, List<StepAction> alternatives,
        String reason, StopAssessment stopAssessment) {

    public StepProposal {
        if (recommended == null || alternatives == null || alternatives.size() > 2
                || reason == null || reason.length() > 2000 || stopAssessment == null) {
            throw new IllegalArgumentException("step proposal is invalid");
        }
        alternatives = List.copyOf(alternatives);
    }
}
