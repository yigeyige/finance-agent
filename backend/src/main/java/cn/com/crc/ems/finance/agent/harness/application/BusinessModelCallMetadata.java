package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 可展示、可审计且不包含模型输入原文的调用元数据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record BusinessModelCallMetadata(String mode, String modelName, String inputDigest,
        String outputDigest, int tokens, long latencyMillis) {

    public BusinessModelCallMetadata {
        requireText(mode, "mode");
        requireText(modelName, "modelName");
        requireDigest(inputDigest, "inputDigest");
        requireDigest(outputDigest, "outputDigest");
        if (tokens < 0 || latencyMillis < 0) {
            throw new IllegalArgumentException("model usage metadata is invalid");
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank() || value.length() > 200) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }

    private static void requireDigest(String value, String field) {
        if (value == null || !value.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
