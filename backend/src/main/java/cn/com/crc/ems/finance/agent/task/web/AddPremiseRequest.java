package cn.com.crc.ems.finance.agent.task.web;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 追加任务前提的 HTTP 请求。 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record AddPremiseRequest(@NotBlank @Size(max = 50) String premiseType,
        @NotBlank @Size(max = 200) String subjectKey, @NotBlank @Size(max = 2000) String statement,
        @NotBlank @Size(max = 2000) String sourceDescription) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
