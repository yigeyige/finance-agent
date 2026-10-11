package cn.com.crc.ems.finance.agent.task.application;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateEvent;

/**
 *
 * @Description: 任务状态事务成功后的新任务快照和状态事件。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record TaskStateTransitionResult(InvestigationTask task, TaskStateEvent event) {

    public TaskStateTransitionResult {
        if (task == null || event == null) {
            throw new IllegalArgumentException("task and event are required");
        }
    }
}
