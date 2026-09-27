package cn.com.crc.ems.finance.agent.task.application;

/**
 *
 * @Description: 单个调查步骤提议的最小输入。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:30:00
 */
public record StepProposalItem(String goal, boolean recommended) {

    public StepProposalItem {
        if (goal == null || goal.isBlank()) {
            throw new IllegalArgumentException("step goal must not be blank");
        }
    }
}
