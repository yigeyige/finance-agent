package cn.com.crc.ems.finance.agent.task.web;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * @Description: 创建任务草稿的 HTTP 请求。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record CreateTaskRequest(@NotBlank @Size(max = 300) String title,
        @NotBlank @Size(max = 8000) String originalQuestion, @NotNull UUID initiatorUserId,
        @NotNull UUID runtimeBundleId, @NotNull UUID businessSceneSkillId) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
