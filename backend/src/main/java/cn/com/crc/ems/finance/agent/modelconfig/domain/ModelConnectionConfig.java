package cn.com.crc.ems.finance.agent.modelconfig.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 管理员维护的单个大模型连接配置，不在领域对象中暴露明文 API Key。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-09 10:00:00
 */
public record ModelConnectionConfig(UUID configId, String name, String provider, String apiUrl, String modelName,
        String reasoningEffort, String apiKeyCiphertext, boolean enabled, UUID createdBy, Instant createdAt,
        UUID updatedBy, Instant updatedAt) {

    public ModelConnectionConfig {
        required(name, 100, "name");
        required(provider, 30, "provider");
        required(apiUrl, 500, "apiUrl");
        required(modelName, 100, "modelName");
        if (!"DEEPSEEK".equals(provider)) {
            throw new IllegalArgumentException("provider is unsupported");
        }
        if (reasoningEffort != null && reasoningEffort.length() > 30) {
            throw new IllegalArgumentException("reasoningEffort is invalid");
        }
    }

    private static void required(String value, int maxLength, String field) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
