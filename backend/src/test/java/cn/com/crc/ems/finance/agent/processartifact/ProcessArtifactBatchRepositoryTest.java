package cn.com.crc.ems.finance.agent.processartifact;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactBatch;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactFile;
import cn.com.crc.ems.finance.agent.processartifact.infrastructure.MybatisProcessArtifactBatchRepository;
import cn.com.crc.ems.finance.agent.processartifact.infrastructure.entity.ProcessArtifactBatchEntity;
import cn.com.crc.ems.finance.agent.processartifact.infrastructure.entity.ProcessArtifactFileEntity;
import cn.com.crc.ems.finance.agent.processartifact.infrastructure.mapper.ProcessArtifactBatchMapper;
import cn.com.crc.ems.finance.agent.processartifact.infrastructure.mapper.ProcessArtifactFileMapper;

/** 文件写入完整后才修剪旧批次；文件写入失败不执行修剪。 */
class ProcessArtifactBatchRepositoryTest {
    @Test
    void prunesOnlyAfterCompleteInsert() {
        var batchMapper = mock(ProcessArtifactBatchMapper.class);
        var fileMapper = mock(ProcessArtifactFileMapper.class);
        var batch = batch();
        when(batchMapper.lockTask(batch.taskId())).thenReturn(batch.taskId());

        new MybatisProcessArtifactBatchRepository(batchMapper, fileMapper).save(batch);

        var order = inOrder(batchMapper, fileMapper);
        order.verify(batchMapper).lockTask(batch.taskId());
        order.verify(batchMapper).insert(any(ProcessArtifactBatchEntity.class));
        order.verify(fileMapper).insert(any(ProcessArtifactFileEntity.class));
        order.verify(batchMapper).deleteOlder(batch.taskId());
    }

    @Test
    void writeFailureLeavesPruneUncalled() {
        var batchMapper = mock(ProcessArtifactBatchMapper.class);
        var fileMapper = mock(ProcessArtifactFileMapper.class);
        var batch = batch();
        when(batchMapper.lockTask(batch.taskId())).thenReturn(batch.taskId());
        when(fileMapper.insert(any(ProcessArtifactFileEntity.class))).thenThrow(new IllegalStateException("文件写入失败"));

        assertThatThrownBy(() -> new MybatisProcessArtifactBatchRepository(batchMapper, fileMapper).save(batch))
                .isInstanceOf(IllegalStateException.class);
        org.mockito.Mockito.verify(batchMapper).lockTask(batch.taskId());
        org.mockito.Mockito.verify(batchMapper).insert(any(ProcessArtifactBatchEntity.class));
        verifyNoMoreInteractions(batchMapper);
    }

    private static ProcessArtifactBatch batch() {
        UUID taskId = UUID.randomUUID();
        UUID batchId = UUID.randomUUID();
        return new ProcessArtifactBatch(batchId, taskId, UUID.randomUUID(), 1, "{}", List.of(),
                "IMPORT_CANDIDATE", Instant.now(), List.of(
                        new ProcessArtifactFile(UUID.randomUUID(), batchId, "MODEL", "流程BPMN.xml", "{}")));
    }
}
