package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.tool.application.SensitiveFieldScanner;

/**
 *
 * @Description: 已按字段策略裁剪、并绑定当前业务任务的模型上下文证据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ContextEvidence(String evidenceId, UUID taskId, JsonNode projectedPayload,
        Set<String> allowedFields) {

    public ContextEvidence {
        if (evidenceId == null || evidenceId.isBlank() || taskId == null || projectedPayload == null
                || !projectedPayload.isObject() || allowedFields == null || allowedFields.isEmpty()) {
            throw new IllegalArgumentException("context evidence is invalid");
        }
        SensitiveFieldScanner.requireSafe(projectedPayload);
        var fields = projectedPayload.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!allowedFields.contains(field)) {
                throw new IllegalArgumentException("evidence field is outside approved projection");
            }
        }
        projectedPayload = projectedPayload.deepCopy();
        allowedFields = Set.copyOf(allowedFields);
    }
}
