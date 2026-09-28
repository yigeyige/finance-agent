package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 根据部署 profile 和外部能力结果生成 Docker Sandbox 状态，不启动本地环境。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 11:20:00
 */
public final class DockerSandboxCapabilityProbe {

    /** 返回无副作用的能力状态；Docker 是否可用由调用方探针结果传入。 */
    public DockerSandboxCapability probe(DockerSandboxProfile profile, boolean dockerAvailable) {
        if (profile == null) {
            throw new IllegalArgumentException("sandbox profile is required");
        }
        if (profile == DockerSandboxProfile.APPLICATION_CONTAINER) {
            return new DockerSandboxCapability(profile, DockerSandboxCapability.Status.NOT_APPLICABLE,
                    "application container does not mount Docker socket");
        }
        return dockerAvailable
                ? new DockerSandboxCapability(profile, DockerSandboxCapability.Status.AVAILABLE,
                        "local Docker capability supplied by caller")
                : new DockerSandboxCapability(profile, DockerSandboxCapability.Status.UNAVAILABLE,
                        "local Docker capability is unavailable");
    }
}
