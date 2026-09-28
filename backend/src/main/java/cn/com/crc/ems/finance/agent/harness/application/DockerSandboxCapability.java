package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: Docker Sandbox 能力探针的安全状态，不执行 Docker 命令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 11:20:00
 */
public record DockerSandboxCapability(DockerSandboxProfile profile, Status status, String reason) {

    public enum Status {
        AVAILABLE,
        UNAVAILABLE,
        NOT_APPLICABLE
    }

    public DockerSandboxCapability {
        if (profile == null || status == null || reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("sandbox capability is invalid");
        }
    }
}
