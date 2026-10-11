package cn.com.crc.ems.finance.agent.tool.application;

import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.application.StepExecutionRequest;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 *
 * @Description: 双层工具守卫的最小请求。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ToolGuardRequest(UUID runtimeBundleId, String toolCode, String toolVersion,
        TaskStatus taskStatus, String environment, StepExecutionRequest execution) {

    public ToolGuardRequest {
        if (runtimeBundleId == null || toolCode == null || toolCode.isBlank() || toolVersion == null
                || toolVersion.isBlank() || taskStatus == null || environment == null || environment.isBlank()
                || execution == null) {
            throw new IllegalArgumentException("tool guard request is invalid");
        }
    }
}
