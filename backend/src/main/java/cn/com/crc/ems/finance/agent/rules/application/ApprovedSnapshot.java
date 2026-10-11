package cn.com.crc.ems.finance.agent.rules.application;

import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.tool.application.SensitiveFieldScanner;

/**
 *
 * @Description: 已通过字段策略裁剪、允许进入确定性规则的快照投影。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ApprovedSnapshot(UUID snapshotId, JsonNode retainedPayload, Set<String> allowedFields) {

    public ApprovedSnapshot {
        if (snapshotId == null || retainedPayload == null || !retainedPayload.isObject()
                || allowedFields == null || allowedFields.isEmpty()) {
            throw new IllegalArgumentException("approved snapshot is invalid");
        }
        SensitiveFieldScanner.requireSafe(retainedPayload);
        List<String> fields = new ArrayList<>();
        retainedPayload.fieldNames().forEachRemaining(fields::add);
        if (!allowedFields.containsAll(fields)) {
            throw new IllegalArgumentException("snapshot contains fields outside approved projection");
        }
        retainedPayload = retainedPayload.deepCopy();
        allowedFields = Set.copyOf(allowedFields);
    }
}
