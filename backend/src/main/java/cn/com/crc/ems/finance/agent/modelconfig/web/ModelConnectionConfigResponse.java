package cn.com.crc.ems.finance.agent.modelconfig.web;

import java.time.Instant;
import java.util.UUID;
import cn.com.crc.ems.finance.agent.modelconfig.domain.ModelConnectionConfig;

/**
 *
 * @Description: 不含明文或密文 API Key 的大模型连接安全视图。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ModelConnectionConfigResponse(UUID configId, String name, String provider, String apiUrl,
        String modelName, String reasoningEffort, boolean enabled, boolean apiKeyConfigured, Instant updatedAt) {
    public static ModelConnectionConfigResponse from(ModelConnectionConfig value) { return new ModelConnectionConfigResponse(value.configId(), value.name(), value.provider(), value.apiUrl(), value.modelName(), value.reasoningEffort(), value.enabled(), value.apiKeyCiphertext() != null && !value.apiKeyCiphertext().isBlank(), value.updatedAt()); }
}
