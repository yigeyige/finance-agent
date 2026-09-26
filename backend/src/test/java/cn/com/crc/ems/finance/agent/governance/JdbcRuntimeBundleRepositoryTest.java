package cn.com.crc.ems.finance.agent.governance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import cn.com.crc.ems.finance.agent.governance.infrastructure.JdbcRuntimeBundleRepository;

class JdbcRuntimeBundleRepositoryTest {

    @Test
    void publishedLookupAddsPublishedStatusPredicate() {
        JdbcTemplate jdbcTemplate = org.mockito.Mockito.mock(JdbcTemplate.class);
        UUID bundleId = UUID.randomUUID();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(bundleId))).thenReturn(List.of());

        new JdbcRuntimeBundleRepository(jdbcTemplate).findPublishedById(bundleId);

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sql.capture(), any(RowMapper.class), eq(bundleId));
        assertThat(sql.getValue()).contains("status = 'PUBLISHED'");
    }
}
