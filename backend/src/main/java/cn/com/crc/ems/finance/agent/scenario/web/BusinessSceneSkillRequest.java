package cn.com.crc.ems.finance.agent.scenario.web;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 业务场景 Skill 新增和编辑的白名单请求。 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record BusinessSceneSkillRequest(@NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 1000) String description,
        @NotBlank @Pattern(regexp = "INVESTIGATE|ADJUST") String handlingType,
        @NotBlank @Size(max = 100) String premiseType) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
