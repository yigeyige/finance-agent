package cn.com.crc.ems.finance.agent.packet.web;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import tools.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 决策包草稿的结构化章节请求。 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record PacketDraftRequest(@NotNull UUID scopeRevisionId, @NotNull UUID runtimeBundleId,
        @NotBlank String problem, @NotNull JsonNode premises, @NotNull JsonNode scope,
        @NotNull JsonNode sources, @NotNull JsonNode facts, @NotNull JsonNode computations,
        @NotNull JsonNode professionalJudgments, @NotNull JsonNode recommendations,
        @NotNull JsonNode businessConfirmations, @NotNull JsonNode validationPreparation,
        @NotNull JsonNode handoff) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
