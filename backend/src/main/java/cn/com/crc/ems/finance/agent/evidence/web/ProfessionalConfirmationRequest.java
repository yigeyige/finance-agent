package cn.com.crc.ems.finance.agent.evidence.web;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import cn.com.crc.ems.finance.agent.evidence.domain.ConfirmationDecision;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @Description: 专业确认请求，强制携带范围、理由和证据引用。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record ProfessionalConfirmationRequest(@NotNull UUID scopeRevisionId,
        @NotNull ConfirmationDecision decision, @NotBlank String rationale, @NotBlank String evidenceRefs) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
