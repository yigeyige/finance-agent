package cn.com.crc.ems.finance.agent.packet.web;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import cn.com.crc.ems.finance.agent.packet.domain.ReviewDecision;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 草稿复核请求，显式绑定摘要、范围和运行版本。 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record PacketReviewRequest(@NotBlank String contentDigest, @NotNull UUID scopeRevisionId,
        @NotNull UUID runtimeBundleId, @NotNull ReviewDecision decision, String comment) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
