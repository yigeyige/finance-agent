package cn.com.crc.ems.finance.agent.businesspacket.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 业务处理证据工作区的单轮累积记录，evidenceJson 保存当轮 EMS 只读查询证据投影。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
public record BusinessEvidenceAccum(UUID evidenceAccumId, UUID taskId, int roundNo, String evidenceJson,
        boolean snapshot, Instant createdAt) {

    public BusinessEvidenceAccum(UUID evidenceAccumId, UUID taskId, int roundNo, String evidenceJson,
            Instant createdAt) {
        this(evidenceAccumId, taskId, roundNo, evidenceJson, false, createdAt);
    }

    public BusinessEvidenceAccum {
        if (evidenceAccumId == null || taskId == null) {
            throw new IllegalArgumentException("evidenceAccumId and taskId are required");
        }
        if (roundNo < 1) {
            throw new IllegalArgumentException("roundNo must be positive");
        }
        if (evidenceJson == null || evidenceJson.isBlank()) {
            throw new IllegalArgumentException("evidenceJson is required");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt is required");
        }
    }
}
