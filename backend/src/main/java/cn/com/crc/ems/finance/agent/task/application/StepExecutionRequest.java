package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 工具调用前业务守卫所需的最小审批绑定请求。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:35:00
 */
public record StepExecutionRequest(UUID taskId, UUID stepId, UUID approvalId, UUID scopeRevisionId,
        UUID sourceBindingId, String goalHash, String fieldPolicyVersion, String queryTemplateVersion,
        String idempotencyKey, Instant requestedAt) {

    public StepExecutionRequest {
        if (taskId == null || stepId == null || approvalId == null || scopeRevisionId == null
                || sourceBindingId == null || requestedAt == null) {
            throw new IllegalArgumentException("execution binding identity is required");
        }
        if (goalHash == null || !goalHash.matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException("goalHash must be a 64-character hexadecimal digest");
        }
        if (fieldPolicyVersion == null || fieldPolicyVersion.isBlank()
                || queryTemplateVersion == null || queryTemplateVersion.isBlank()
                || idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("execution binding versions and idempotencyKey are required");
        }
    }
}
