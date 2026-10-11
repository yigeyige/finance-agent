package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Set;

/**
 *
 * @Description: 不含模型原文的可审计调用元数据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ModelMetadata(String modelBindingVersion, String templateVersion,
        Set<String> inputEvidenceIds, String inputFieldDigest, String inputDigest,
        String outputDigest, int tokens, long latencyMillis, String finishReason,
        boolean schemaValid) {

    public ModelMetadata {
        requireText(modelBindingVersion, "modelBindingVersion");
        requireText(templateVersion, "templateVersion");
        inputEvidenceIds = inputEvidenceIds == null ? Set.of() : Set.copyOf(inputEvidenceIds);
        requireText(inputFieldDigest, "inputFieldDigest");
        requireText(inputDigest, "inputDigest");
        requireText(outputDigest, "outputDigest");
        if (tokens < 0 || latencyMillis < 0 || !schemaValid) {
            throw new IllegalArgumentException("model metadata is invalid");
        }
        requireText(finishReason, "finishReason");
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
