package cn.com.crc.ems.finance.agent.task.application;

import java.util.List;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/**
 *
 * @Description: 步骤提议应用结果，不包含模型原文或能力 permit。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:30:00
 */
public record StepProposalResult(InvestigationTask task, List<InvestigationStep> steps) {

    public StepProposalResult {
        if (task == null || steps == null) {
            throw new IllegalArgumentException("task and steps are required");
        }
        steps = List.copyOf(steps);
    }
}
