package cn.com.crc.ems.finance.agent.processartifact.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.processartifact.application.ProcessArtifactBatchRepository;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactBatch;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactFile;
import cn.com.crc.ems.finance.agent.processartifact.infrastructure.entity.ProcessArtifactBatchEntity;
import cn.com.crc.ems.finance.agent.processartifact.infrastructure.entity.ProcessArtifactFileEntity;
import cn.com.crc.ems.finance.agent.processartifact.infrastructure.mapper.ProcessArtifactBatchMapper;
import cn.com.crc.ems.finance.agent.processartifact.infrastructure.mapper.ProcessArtifactFileMapper;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/** 同任务串行写入，并在完整写入后只保留最近两批。 */
@Repository
public class MybatisProcessArtifactBatchRepository implements ProcessArtifactBatchRepository {
    private final ProcessArtifactBatchMapper batches;
    private final ProcessArtifactFileMapper files;

    public MybatisProcessArtifactBatchRepository(ProcessArtifactBatchMapper batches, ProcessArtifactFileMapper files) {
        this.batches = batches;
        this.files = files;
    }

    @Override
    @Transactional
    public ProcessArtifactBatch save(ProcessArtifactBatch batch) {
        if (batches.lockTask(batch.taskId()) == null) throw new IllegalStateException("业务处理任务不存在");
        ProcessArtifactBatchEntity entity = new ProcessArtifactBatchEntity();
        entity.batchId = PersistenceUuid.toStorage(batch.batchId());
        entity.taskId = batch.taskId();
        entity.packetId = batch.packetId();
        entity.packetRevision = batch.packetRevision();
        entity.planJson = batch.planJson();
        entity.missingItemsJson = CanonicalJsonCodec.canonicalize(
                cn.com.crc.ems.finance.agent.shared.domain.JsonCodec.mapper().valueToTree(batch.missingItems()));
        entity.importStatus = batch.importStatus();
        entity.createdAt = batch.createdAt();
        batches.insert(entity);
        for (ProcessArtifactFile file : batch.files()) {
            ProcessArtifactFileEntity item = new ProcessArtifactFileEntity();
            item.fileId = PersistenceUuid.toStorage(file.fileId());
            item.batchId = batch.batchId();
            item.kind = file.kind();
            item.fileName = file.fileName();
            item.contentText = file.contentText();
            files.insert(item);
        }
        batches.deleteOlder(batch.taskId());
        return batch;
    }

    @Override
    public List<ProcessArtifactBatch> findRecentByTaskId(UUID taskId) {
        List<ProcessArtifactBatch> result = new ArrayList<>();
        for (ProcessArtifactBatchEntity batch : batches.selectList(new QueryWrapper<ProcessArtifactBatchEntity>()
                .eq("task_id", taskId).orderByDesc("created_at", "batch_id").last("LIMIT 2"))) {
            UUID batchId = PersistenceUuid.toDomain(batch.batchId);
            List<ProcessArtifactFile> contents = files.selectList(new QueryWrapper<ProcessArtifactFileEntity>()
                    .eq("batch_id", batchId).orderByAsc("kind")).stream()
                    .map(file -> new ProcessArtifactFile(PersistenceUuid.toDomain(file.fileId), batchId,
                            file.kind, file.fileName, file.contentText)).toList();
            List<String> missing = new ArrayList<>();
            CanonicalJsonCodec.parse(batch.missingItemsJson).forEach(item -> missing.add(item.asText()));
            result.add(new ProcessArtifactBatch(batchId, batch.taskId, batch.packetId, batch.packetRevision,
                    batch.planJson, List.copyOf(missing), batch.importStatus, batch.createdAt, contents));
        }
        return result;
    }
}
