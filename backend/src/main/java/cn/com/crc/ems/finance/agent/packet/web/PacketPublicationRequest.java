package cn.com.crc.ems.finance.agent.packet.web;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.JsonNode;

/** 发布请求，携带批准基线和发布前全量重查结果。 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record PacketPublicationRequest(@NotNull UUID draftId, @NotNull UUID publicationId,
        @NotBlank String contentDigest, @NotNull UUID scopeRevisionId, @NotNull UUID runtimeBundleId,
        @NotBlank String canonicalJson, @NotBlank String htmlSnapshot, @NotBlank String sourceVersions,
        @NotBlank String coverageSummary, @NotNull List<JsonNode> baseline,
        @NotNull List<JsonNode> current) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
