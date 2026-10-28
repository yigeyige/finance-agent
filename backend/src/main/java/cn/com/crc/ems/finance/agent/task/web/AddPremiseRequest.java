package cn.com.crc.ems.finance.agent.task.web;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * @Description: 追加任务前提的 HTTP 请求。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record AddPremiseRequest(@NotBlank @Size(max = 200) String subjectKey) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
