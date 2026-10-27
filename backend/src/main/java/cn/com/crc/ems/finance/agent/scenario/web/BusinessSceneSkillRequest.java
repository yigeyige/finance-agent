package cn.com.crc.ems.finance.agent.scenario.web;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * @Description: 业务场景 Skill 新增和编辑的白名单请求。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-27 11:20:00
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record BusinessSceneSkillRequest(@NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 2000) String investigationPrompt,
        @NotBlank @Size(max = 2000) String decisionPacketPrompt) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
