package cn.com.crc.ems.finance.agent.processartifact.application;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactBatch;

/** 文件批次存储：成功时原子保留最新两批。 */
public interface ProcessArtifactBatchRepository {
    ProcessArtifactBatch save(ProcessArtifactBatch batch);

    List<ProcessArtifactBatch> findRecentByTaskId(UUID taskId);
}
