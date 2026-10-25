package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.tool.application.SensitiveFieldScanner;

/**
 *
 * @Description: 送入模型的最小字段投影上下文，只保留证据引用、获准字段和任务元数据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record AllowedContext(JsonNode projectedEvidence, Set<String> evidenceIds,
        Set<String> allowedFields, Map<String, String> taskMetadata) {

    public AllowedContext {
        if (projectedEvidence == null || !projectedEvidence.isObject()) {
            throw new IllegalArgumentException("projectedEvidence must be a JSON object");
        }
        SensitiveFieldScanner.requireSafe(projectedEvidence);
        evidenceIds = immutableTextSet(evidenceIds, "evidenceIds");
        allowedFields = immutableTextSet(allowedFields, "allowedFields");
        taskMetadata = taskMetadata == null ? Map.of() : Map.copyOf(taskMetadata);
        taskMetadata.forEach((key, value) -> {
            requireText(key, "taskMetadata key");
            requireText(value, "taskMetadata value");
        });
        projectedEvidence = projectedEvidence.deepCopy();
    }

    /** 返回用于 fixture 定位和审计的稳定上下文摘要，不返回上下文原文。 */
    public String digest() {
        String canonical = CanonicalJsonCodec.canonicalize(projectedEvidence)
                + "|" + String.join(",", evidenceIds.stream().sorted().toList())
                + "|" + String.join(",", allowedFields.stream().sorted().toList())
                + "|" + taskMetadata.entrySet().stream().sorted(Map.Entry.comparingByKey())
                        .map(entry -> entry.getKey() + "=" + entry.getValue()).toList();
        return Sha256Digest.hex(canonical);
    }

    /** 返回仅由允许字段名称组成的摘要，供调用元数据追踪字段清单。 */
    public String fieldDigest() {
        return digestText(String.join(",", allowedFields.stream().sorted().toList()));
    }

    private static Set<String> immutableTextSet(Set<String> values, String field) {
        if (values == null) {
            throw new IllegalArgumentException(field + " is required");
        }
        Set<String> copy = Set.copyOf(values);
        copy.forEach(value -> requireText(value, field + " item"));
        return copy;
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank() || value.length() > 200) {
            throw new IllegalArgumentException(field + " must be non-blank and at most 200 characters");
        }
    }

    private static String digestText(String value) {
        return Sha256Digest.hex(value);
    }
}
