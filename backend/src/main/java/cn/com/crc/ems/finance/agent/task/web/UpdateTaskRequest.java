package cn.com.crc.ems.finance.agent.task.web;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * @Description: 更新任务基础信息的 HTTP 请求。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-27 20:00:00
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record UpdateTaskRequest(@NotBlank @Size(max = 8000) String originalQuestion,
        UUID businessSceneSkillId, UUID databaseConfigId) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
