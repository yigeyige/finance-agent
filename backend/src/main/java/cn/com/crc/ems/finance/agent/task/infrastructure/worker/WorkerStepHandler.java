package cn.com.crc.ems.finance.agent.task.infrastructure.worker;

import cn.com.crc.ems.finance.agent.task.application.TaskLeaseClaim;

/**
 *
 * @Description: Worker 执行步骤的可插拔处理器端口，具体工具/模型编排由后续任务提供。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-28 10:20:00
 */
@FunctionalInterface
public interface WorkerStepHandler {

    /**
     *
     * @description: 在 Worker 线程中执行已持有租约的步骤，处理器应在安全点自行调用心跳。
     *
     * @param: claim 已完成原子领取的步骤和租约
     */
    void execute(TaskLeaseClaim claim);
}
