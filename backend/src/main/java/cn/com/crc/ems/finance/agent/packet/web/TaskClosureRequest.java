package cn.com.crc.ems.finance.agent.packet.web;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;

/** 调查关闭请求，关闭原因仅用于审计摘要。 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record TaskClosureRequest(@NotBlank String closureReason) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
