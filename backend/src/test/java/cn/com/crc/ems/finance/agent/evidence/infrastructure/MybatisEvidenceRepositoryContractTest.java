package cn.com.crc.ems.finance.agent.evidence.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;
import cn.com.crc.ems.finance.agent.evidence.domain.FactSnapshot;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.FactSnapshotEntity;
import cn.com.crc.ems.finance.agent.evidence.infrastructure.mapper.FactSnapshotMapper;

class MybatisEvidenceRepositoryContractTest {

    @Test
    void factSnapshotRepositoryUsesMapperAndExplicitMapping() {
        FactSnapshotMapper mapper = org.mockito.Mockito.mock(FactSnapshotMapper.class);
        UUID snapshotId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        FactSnapshotEntity entity = new FactSnapshotEntity();
        entity.snapshotId = snapshotId.toString();
        entity.taskId = taskId;
        entity.stepId = UUID.randomUUID();
        entity.sourceBindingId = UUID.randomUUID();
        entity.sourceObjectType = "ROLE";
        entity.sourceObjectKey = "role-1";
        entity.allowedFields = "{\"code\":\"A\"}";
        entity.checkedAt = Instant.parse("2026-09-26T00:00:00Z");
        entity.effectiveClass = EffectiveClass.CURRENT_EFFECTIVE.name();
        entity.resultDigest = "a".repeat(64);
        entity.fieldPolicyVersion = "fields-v1";
        entity.schemaVersion = "scope-v1";
        entity.createdAt = entity.checkedAt;
        entity.updatedAt = entity.checkedAt;
        when(mapper.selectList(any(QueryWrapper.class))).thenReturn(java.util.List.of(entity));

        MybatisFactSnapshotRepository repository = new MybatisFactSnapshotRepository(mapper);
        assertThat(repository.findByTaskId(taskId)).hasSize(1);
        verify(mapper).selectList(any(QueryWrapper.class));
    }
}
