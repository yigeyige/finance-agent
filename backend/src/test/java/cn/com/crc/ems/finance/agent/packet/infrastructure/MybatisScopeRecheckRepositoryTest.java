package cn.com.crc.ems.finance.agent.packet.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.packet.application.RecheckDecision;
import cn.com.crc.ems.finance.agent.packet.domain.ScopeRecheckRecord;
import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.ScopeRecheckEntity;
import cn.com.crc.ems.finance.agent.packet.infrastructure.mapper.ScopeRecheckMapper;

class MybatisScopeRecheckRepositoryTest {

    @Test
    void persistsAndReadsLatestRecheckRecord() {
        ScopeRecheckMapper mapper = org.mockito.Mockito.mock(ScopeRecheckMapper.class);
        Instant now = Instant.parse("2026-09-29T00:00:00Z");
        UUID taskId = UUID.randomUUID();
        ScopeRecheckRecord record = new ScopeRecheckRecord(UUID.randomUUID(), taskId, UUID.randomUUID(), now,
                RecheckDecision.STALE_DRAFT, "a".repeat(64), "[added]", UUID.randomUUID(), now, UUID.randomUUID(), now);
        ScopeRecheckEntity entity = MybatisPacketMappings.toEntity(record);
        when(mapper.selectOne(any(QueryWrapper.class))).thenReturn(entity);

        MybatisScopeRecheckRepository repository = new MybatisScopeRecheckRepository(mapper);
        repository.insert(record);
        assertThat(repository.findLatestByTaskId(taskId)).contains(record);
        verify(mapper).insert(any(ScopeRecheckEntity.class));
        verify(mapper).selectOne(any(QueryWrapper.class));
    }
}
