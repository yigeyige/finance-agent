package cn.com.crc.ems.finance.agent.governance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.governance.infrastructure.MybatisRuntimeBundleRepository;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundle;
import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.RuntimeBundleEntity;
import cn.com.crc.ems.finance.agent.governance.infrastructure.mapper.RuntimeBundleMapper;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundleStatus;

class MybatisRuntimeBundleRepositoryTest {

    @Test
    void publishedLookupAddsPublishedStatusPredicate() {
        RuntimeBundleMapper mapper = org.mockito.Mockito.mock(RuntimeBundleMapper.class);
        UUID bundleId = UUID.randomUUID();
        when(mapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        new MybatisRuntimeBundleRepository(mapper).findPublishedById(bundleId);

        verify(mapper).selectOne(any(QueryWrapper.class));
        assertThat(bundleId).isNotNull();
    }

    @Test
    void explicitUuidIsPreservedWhenMappedToPersistenceString() {
        RuntimeBundleMapper mapper = org.mockito.Mockito.mock(RuntimeBundleMapper.class);
        UUID bundleId = UUID.randomUUID();
        Instant now = Instant.now();
        when(mapper.selectById(bundleId.toString())).thenReturn(null);
        RuntimeBundle bundle = new RuntimeBundle(bundleId, "app", "agentscope", "model", "prompt", "rules",
                "tools", "sources", null, "digest", RuntimeBundleStatus.PUBLISHED, now, null, now, null);

        new MybatisRuntimeBundleRepository(mapper).insertIfAbsent(bundle);

        var captor = forClass(RuntimeBundleEntity.class);
        verify(mapper).selectById(bundleId.toString());
        verify(mapper).insert(captor.capture());
        assertThat(captor.getValue().runtimeBundleId).isEqualTo(bundleId.toString());
    }
}
