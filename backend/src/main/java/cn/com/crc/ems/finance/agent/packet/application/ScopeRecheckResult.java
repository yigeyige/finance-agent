package cn.com.crc.ems.finance.agent.packet.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.domain.PacketDraftStatus;

/** 发布前全范围重查结果。 */
public record ScopeRecheckResult(UUID taskId, UUID scopeRevisionId, Instant checkedAt,
        List<ScopeRecheckDiff> diffs, RecheckDecision decision, PacketDraftStatus draftStatus,
        String resultDigest) {

    public ScopeRecheckResult {
        if (taskId == null || scopeRevisionId == null || checkedAt == null || diffs == null
                || decision == null || draftStatus == null || resultDigest == null || resultDigest.isBlank()) {
            throw new IllegalArgumentException("scope recheck result is invalid");
        }
        diffs = List.copyOf(diffs);
    }
}
