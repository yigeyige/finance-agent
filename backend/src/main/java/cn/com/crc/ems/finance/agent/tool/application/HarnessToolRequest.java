package cn.com.crc.ems.finance.agent.tool.application;

import java.util.UUID;

/** Harness 请求转入业务工具网关前的最小、不可变请求。 */
public record HarnessToolRequest(UUID requestId, String toolCode, ToolGuardRequest guardRequest) {

    public HarnessToolRequest {
        if (requestId == null || toolCode == null || toolCode.isBlank() || guardRequest == null
                || !toolCode.equals(guardRequest.toolCode())) {
            throw new IllegalArgumentException("harness tool request is invalid");
        }
    }
}
