package cn.com.crc.ems.finance.agent.packet.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.packet.domain.PacketDraftStatus;
import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.PacketDraftEntity;
import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.PacketRevisionEntity;
import cn.com.crc.ems.finance.agent.packet.infrastructure.mapper.PacketDraftMapper;
import cn.com.crc.ems.finance.agent.packet.infrastructure.mapper.PacketRevisionMapper;

class MybatisPacketRepositoryContractTest {

    @Test
    void draftRepositoryUsesCurrentStatusAndExplicitJsonMapping() {
        PacketDraftMapper mapper = org.mockito.Mockito.mock(PacketDraftMapper.class);
        PacketDraftEntity entity = new PacketDraftEntity();
        entity.draftId = UUID.randomUUID().toString();
        entity.taskId = UUID.randomUUID();
        entity.draftNo = 1;
        entity.scopeRevisionId = UUID.randomUUID();
        entity.runtimeBundleId = UUID.randomUUID();
        entity.canonicalContent = "{\"findingCount\":1}";
        entity.contentDigest = "a".repeat(64);
        entity.status = PacketDraftStatus.CURRENT.name();
        entity.createdAt = Instant.parse("2026-09-27T00:00:00Z");
        entity.updatedAt = entity.createdAt;
        when(mapper.selectOne(any(QueryWrapper.class))).thenReturn(entity);

        MybatisPacketDraftRepository repository = new MybatisPacketDraftRepository(mapper);
        assertThat(repository.findCurrentByTaskId(entity.taskId)).isPresent()
                .get().extracting("contentDigest").isEqualTo(entity.contentDigest);
        verify(mapper).selectOne(any(QueryWrapper.class));
    }

    @Test
    void revisionRepositoryQueriesOnlyPublishedRows() {
        PacketRevisionMapper mapper = org.mockito.Mockito.mock(PacketRevisionMapper.class);
        PacketRevisionEntity entity = new PacketRevisionEntity();
        entity.packetRevisionId = UUID.randomUUID().toString();
        entity.taskId = UUID.randomUUID();
        entity.revisionNo = 1;
        entity.draftId = UUID.randomUUID();
        entity.canonicalContent = "{}";
        entity.htmlSnapshot = "<html></html>";
        entity.contentDigest = "b".repeat(64);
        entity.checkedAt = Instant.parse("2026-09-27T00:00:00Z");
        entity.sourceVersions = "{}";
        entity.coverageSummary = "{}";
        entity.publishedBy = UUID.randomUUID();
        entity.publishedAt = entity.checkedAt;
        entity.createdAt = entity.checkedAt;
        entity.updatedAt = entity.checkedAt;
        when(mapper.selectOne(any(QueryWrapper.class))).thenReturn(entity);

        MybatisPacketRevisionRepository repository = new MybatisPacketRevisionRepository(mapper);
        assertThat(repository.findPublishedByTaskId(entity.taskId)).isPresent();
        verify(mapper).selectOne(any(QueryWrapper.class));
    }
}
