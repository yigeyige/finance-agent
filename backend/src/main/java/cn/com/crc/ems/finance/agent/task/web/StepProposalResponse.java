package cn.com.crc.ems.finance.agent.task.web;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.application.StepProposalResult;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;

/**
 *
 * @Description: 步骤提议最小响应，供人工审批卡展示。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:45:00
 */
public record StepProposalResponse(UUID taskId, String status, long rowVersion, UUID scopeRevisionId,
        UUID currentStepId, List<StepView> steps) {

    public static StepProposalResponse from(StepProposalResult result) {
        return new StepProposalResponse(result.task().taskId(), result.task().status().name(), result.task().rowVersion(),
                result.task().currentScopeRevisionId(), result.task().currentStepId(),
                result.steps().stream().map(StepView::from).toList());
    }

    /** 步骤审批卡展示所需字段。 */
    public record StepView(UUID stepId, int sequenceNo, String goal, boolean recommended, String status) {
        public static StepView from(InvestigationStep step) {
            return new StepView(step.stepId(), step.sequenceNo(), step.goal(), step.recommended(), step.status().name());
        }
    }
}
