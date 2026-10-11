package cn.com.crc.ems.finance.agent.task.application;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;

/**
 *
 * @Description: 一个已在同一数据库事务中完成状态推进的步骤领取结果。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record TaskLeaseClaim(InvestigationStep step, TaskLease lease) {

    public TaskLeaseClaim {
        if (step == null || lease == null) {
            throw new IllegalArgumentException("step and lease are required");
        }
        if (!step.stepId().equals(lease.stepId()) || !step.taskId().equals(lease.taskId())) {
            throw new IllegalArgumentException("step and lease identity must match");
        }
    }
}
