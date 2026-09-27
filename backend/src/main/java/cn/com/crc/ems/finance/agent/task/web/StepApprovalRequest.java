package cn.com.crc.ems.finance.agent.task.web;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import cn.com.crc.ems.finance.agent.task.application.ApprovalDecision;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 *
 * @Description: 冻结步骤执行边界的审批 REST 请求。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 23:45:00
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record StepApprovalRequest(@NotNull UUID stepId, @NotBlank @Pattern(regexp = "^[a-fA-F0-9]{64}$") String goalHash,
        @NotNull UUID sourceBindingId, @NotNull UUID scopeRevisionId,
        @NotBlank @Size(max = 80) String fieldPolicyVersion,
        @NotBlank @Size(max = 80) String queryTemplateVersion, @Min(1) int maxCalls,
        Instant expiresAt, @Min(0) long expectedRowVersion, @NotNull ApprovalDecision decision) {

    @JsonAnySetter
    public void rejectUnknown(String name, Object value) {
        throw new IllegalArgumentException("unknown request field: " + name);
    }
}
