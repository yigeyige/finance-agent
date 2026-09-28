package cn.com.crc.ems.finance.agent.tool.application;

import cn.com.crc.ems.finance.agent.task.application.ExecutionPermit;

/** 进程内工具 permit 及其签名；禁止作为 HTTP 或模型令牌传输。 */
public record SignedToolPermit(ExecutionPermit permit, String toolCode, String toolVersion, String signature) {

    public SignedToolPermit {
        if (permit == null || toolCode == null || toolCode.isBlank() || toolVersion == null || toolVersion.isBlank()
                || signature == null || signature.isBlank()) {
            throw new IllegalArgumentException("signed tool permit is invalid");
        }
    }
}
