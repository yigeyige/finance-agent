package cn.com.crc.ems.finance.agent.harness.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** 只从当前任务的裁剪证据和白名单元数据构造 AllowedContext。 */
public final class AllowedContextFactory {

    private AllowedContextFactory() {
    }

    /** 合并证据并拒绝跨任务、字段越权、冲突值和敏感字段。 */
    public static AllowedContext create(UUID taskId, List<ContextEvidence> evidence,
            Map<String, String> taskMetadata, Set<String> confirmedFields) {
        if (taskId == null || evidence == null || evidence.isEmpty() || confirmedFields == null) {
            throw new IllegalArgumentException("taskId, evidence and confirmedFields are required");
        }
        ObjectNode projected = JsonNodeFactory.instance.objectNode();
        Set<String> evidenceIds = new LinkedHashSet<>();
        Set<String> allowedFields = new HashSet<>(confirmedFields);
        for (ContextEvidence item : evidence) {
            if (!taskId.equals(item.taskId()) || !evidenceIds.add(item.evidenceId())) {
                throw new IllegalArgumentException("evidence is cross-task or duplicated");
            }
            if (!allowedFields.containsAll(item.allowedFields())) {
                throw new IllegalArgumentException("evidence fields are not confirmed for this context");
            }
            item.projectedPayload().fields().forEachRemaining(entry -> {
                JsonNode existing = projected.get(entry.getKey());
                if (existing != null && !existing.equals(entry.getValue())) {
                    throw new IllegalArgumentException("evidence contains conflicting field values");
                }
                projected.set(entry.getKey(), entry.getValue().deepCopy());
            });
        }
        return new AllowedContext(projected, evidenceIds, Set.copyOf(allowedFields), taskMetadata);
    }
}
