package cn.com.crc.ems.finance.agent.tool.application;

import java.util.Set;

import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 *
 * @Description: 冻结在 runtime bundle 中的工具描述。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ToolDescriptor(String toolCode, String version, String inputSchemaRef,
        String outputSchemaRef, String riskLevel, Set<TaskStatus> allowedStatuses,
        boolean approvalRequired, long timeoutMillis, long maxResultBytes, Set<String> environmentAllowlist,
        boolean retired) {

    public ToolDescriptor {
        requireText(toolCode, "toolCode");
        requireText(version, "version");
        requireText(inputSchemaRef, "inputSchemaRef");
        requireText(outputSchemaRef, "outputSchemaRef");
        requireText(riskLevel, "riskLevel");
        if (allowedStatuses == null || allowedStatuses.isEmpty() || timeoutMillis < 1 || maxResultBytes < 1
                || environmentAllowlist == null || environmentAllowlist.isEmpty()) {
            throw new IllegalArgumentException("tool descriptor limits and allowlists are invalid");
        }
        allowedStatuses = Set.copyOf(allowedStatuses);
        environmentAllowlist = Set.copyOf(environmentAllowlist);
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
