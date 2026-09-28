package cn.com.crc.ems.finance.agent.harness.application;

import java.util.UUID;

/** 将 Harness session 绑定到业务任务、用户、运行版本和已确认范围。 */
public record HarnessSessionBinding(UUID sessionId, UUID taskId, UUID userId,
        UUID runtimeBundleId, UUID scopeRevisionId) {

    public HarnessSessionBinding {
        requireId(sessionId, "sessionId");
        requireId(taskId, "taskId");
        requireId(userId, "userId");
        requireId(runtimeBundleId, "runtimeBundleId");
        requireId(scopeRevisionId, "scopeRevisionId");
    }

    private static void requireId(UUID value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " is required");
        }
    }
}
