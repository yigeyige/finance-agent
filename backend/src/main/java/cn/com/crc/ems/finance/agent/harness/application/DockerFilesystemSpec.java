package cn.com.crc.ems.finance.agent.harness.application;

import java.nio.file.Path;
import java.util.Set;

/**
 *
 * @Description: DockerFilesystemSpec 的无副作用安全契约和资源边界描述。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 11:20:00
 */
public record DockerFilesystemSpec(DockerSandboxProfile profile, boolean enabled, boolean networkEnabled,
        Set<String> environmentAllowlist, Set<Path> mounts, long cpuLimitMillis, long memoryLimitBytes,
        long timeoutMillis, boolean dockerSocketMountAllowed, String disabledReason) {

    private static final Set<String> SAFE_ENVIRONMENT = Set.of("LANG", "TZ");

    public DockerFilesystemSpec {
        if (profile == null || environmentAllowlist == null || mounts == null || disabledReason == null) {
            throw new IllegalArgumentException("sandbox spec is invalid");
        }
        environmentAllowlist = Set.copyOf(environmentAllowlist);
        mounts = Set.copyOf(mounts);
        if (!SAFE_ENVIRONMENT.containsAll(environmentAllowlist) || networkEnabled || !mounts.isEmpty()
                || dockerSocketMountAllowed) {
            throw new IllegalArgumentException("sandbox access boundary is unsafe");
        }
        if (enabled && (cpuLimitMillis < 1 || memoryLimitBytes < 1 || timeoutMillis < 1)) {
            throw new IllegalArgumentException("enabled sandbox limits must be positive");
        }
        if (!enabled && disabledReason.isBlank()) {
            throw new IllegalArgumentException("disabled sandbox requires a reason");
        }
    }

    /** 返回仅供 JAR/local 测试使用的受限 Sandbox 描述。 */
    public static DockerFilesystemSpec jarLocal() {
        return new DockerFilesystemSpec(DockerSandboxProfile.JAR_LOCAL, true, false, SAFE_ENVIRONMENT, Set.of(),
                1_000, 128L * 1024 * 1024, 10_000, false, "");
    }

    /** 返回应用镜像内明确禁用 Docker Sandbox 的描述。 */
    public static DockerFilesystemSpec applicationContainer() {
        return new DockerFilesystemSpec(DockerSandboxProfile.APPLICATION_CONTAINER, false, false, Set.of(), Set.of(),
                0, 0, 0, false, "application container does not mount Docker socket");
    }
}
