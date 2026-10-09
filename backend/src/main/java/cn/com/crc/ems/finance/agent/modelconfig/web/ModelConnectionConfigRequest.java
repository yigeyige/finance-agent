package cn.com.crc.ems.finance.agent.modelconfig.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 大模型连接新增或编辑请求，API Key 只允许写入。 */
public record ModelConnectionConfigRequest(@NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 30) String provider, @NotBlank @Size(max = 500) String apiUrl,
        @NotBlank @Size(max = 100) String modelName, @Size(max = 30) String reasoningEffort,
        @Size(max = 1000) String apiKey) {
}
