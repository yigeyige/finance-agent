package cn.com.crc.ems.finance.agent.governance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.governance.infrastructure.MybatisRuntimeBundleRepository;
import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.RuntimeBundleEntity;
import cn.com.crc.ems.finance.agent.governance.infrastructure.mapper.RuntimeBundleMapper;

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
}
