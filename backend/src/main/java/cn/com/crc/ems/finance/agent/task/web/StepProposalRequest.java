package cn.com.crc.ems.finance.agent.task.web;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * @Description: 步骤提议 REST 请求，限制一个推荐和最多两个备选。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:45:00
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record StepProposalRequest(@NotEmpty @Size(max = 3) List<@Valid Item> steps) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }

    /** 单个推荐/备选步骤。 */
    @JsonIgnoreProperties(ignoreUnknown = false)
    public record Item(@NotBlank @Size(max = 1000) String goal, @NotNull Boolean recommended) {

        @JsonAnySetter
        public void rejectUnknown(String name, Object value) {
            throw new IllegalArgumentException("unknown request field: " + name);
        }
    }
}
