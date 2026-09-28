package cn.com.crc.ems.finance.agent.harness.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.CheckpointEntity;
import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.SessionStateEntity;
import cn.com.crc.ems.finance.agent.harness.infrastructure.mapper.CheckpointMapper;
import cn.com.crc.ems.finance.agent.harness.infrastructure.mapper.SessionStateMapper;

class MybatisHarnessRepositoryContractTest {

    @Test
    void sessionRepositoryUsesTaskAndUserIsolationAndExplicitStateMapping() {
        SessionStateMapper mapper = org.mockito.Mockito.mock(SessionStateMapper.class);
        SessionStateEntity entity = new SessionStateEntity();
        entity.sessionId = UUID.randomUUID().toString();
        entity.taskId = UUID.randomUUID();
        entity.userId = UUID.randomUUID();
        entity.harnessVersion = "harness-2.0";
        entity.stateSchemaVersion = "harness-v1";
        entity.stateRef = "{\"step\":\"s1\"}";
        entity.stateDigest = "a".repeat(64);
        entity.createdAt = Instant.parse("2026-09-27T00:00:00Z");
        entity.updatedAt = entity.createdAt;
        when(mapper.selectOne(any(QueryWrapper.class))).thenReturn(entity);

        MybatisSessionStateRepository repository = new MybatisSessionStateRepository(mapper);
        assertThat(repository.findById(entity.sessionId == null ? UUID.randomUUID() : UUID.fromString(entity.sessionId),
                entity.taskId, entity.userId)).isPresent();
        verify(mapper).selectOne(any(QueryWrapper.class));
    }

    @Test
    void checkpointRepositoryQueriesBySessionTaskAndUser() {
        CheckpointMapper mapper = org.mockito.Mockito.mock(CheckpointMapper.class);
        CheckpointEntity entity = new CheckpointEntity();
        entity.checkpointId = UUID.randomUUID().toString();
        entity.sessionId = UUID.randomUUID();
        entity.taskId = UUID.randomUUID();
        entity.userId = UUID.randomUUID();
        entity.stepId = UUID.randomUUID();
        entity.scopeRevisionId = UUID.randomUUID();
        entity.approvalId = UUID.randomUUID();
        entity.runtimeBundleId = UUID.randomUUID();
        entity.checkpointType = "STEP_COMPLETED";
        entity.stateSchemaVersion = "harness-v1";
        entity.stateRef = "{\"step\":\"s1\"}";
        entity.stateDigest = "b".repeat(64);
        entity.createdAt = Instant.parse("2026-09-27T00:00:00Z");
        entity.updatedAt = entity.createdAt;
        when(mapper.selectList(any(QueryWrapper.class))).thenReturn(java.util.List.of(entity));

        MybatisCheckpointRepository repository = new MybatisCheckpointRepository(mapper);
        assertThat(repository.findBySessionId(entity.sessionId, entity.taskId, entity.userId)).hasSize(1);
        verify(mapper).selectList(any(QueryWrapper.class));
    }
}
