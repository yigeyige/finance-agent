package cn.com.crc.ems.finance.agent.processartifact.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactBatch;

/** 文件批次列表只暴露文件元数据，正文走下载接口。 */
public record ProcessArtifactBatchResponse(UUID batchId, UUID packetId, int packetRevision,
        String importStatus, List<String> missingItems, Instant createdAt, List<FileInfo> files) {
    public static ProcessArtifactBatchResponse from(ProcessArtifactBatch batch) {
        return new ProcessArtifactBatchResponse(batch.batchId(), batch.packetId(), batch.packetRevision(),
                batch.importStatus(), batch.missingItems(), batch.createdAt(),
                batch.files().stream().map(file -> new FileInfo(file.fileId(), file.kind(), file.fileName())).toList());
    }

    public record FileInfo(UUID fileId, String kind, String fileName) { }
}
