package cn.com.crc.ems.finance.agent.packet.web;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;

/** 决策包交接请求，拒绝未声明字段。 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record PacketHandoffRequest(@NotBlank String ownerReference, String externalProcessReference,
        @NotBlank String downstreamStatus) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
