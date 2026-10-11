package cn.com.crc.ems.finance.agent.task.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import tools.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * @Description: 确认不可变范围修订的 HTTP 请求，JSON 字段按文本接收后严格解析。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record ConfirmScopeRequest(@NotBlank @Size(max = 100) String systemCode,
        @NotNull JsonNode objectSelector, @NotNull JsonNode effectiveTimeRange, @NotNull JsonNode includedPaths,
        @NotNull JsonNode excludedPaths, @NotNull JsonNode allowedReadFields,
        @NotNull JsonNode allowedRetainFields, @NotNull JsonNode allowedModelFields,
        @NotBlank @Size(max = 30) String schemaVersion) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
