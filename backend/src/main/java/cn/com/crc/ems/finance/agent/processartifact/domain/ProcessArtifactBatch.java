package cn.com.crc.ems.finance.agent.processartifact.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** 固定决策包修订对应的文件批次。 */
public record ProcessArtifactBatch(UUID batchId, UUID taskId, UUID packetId, int packetRevision,
        String planJson, List<String> missingItems, String importStatus, Instant createdAt,
        List<ProcessArtifactFile> files) { }
