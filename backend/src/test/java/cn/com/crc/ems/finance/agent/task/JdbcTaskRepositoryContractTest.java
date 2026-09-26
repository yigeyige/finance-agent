package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.JdbcInvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.infrastructure.JdbcTaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.infrastructure.JdbcToolInvocationRepository;

class JdbcTaskRepositoryContractTest {

    @Test
    void taskUpdateUsesOptimisticRowVersionPredicate() {
        JdbcTemplate jdbcTemplate = org.mockito.Mockito.mock(JdbcTemplate.class);
        InvestigationTask task = new InvestigationTask(UUID.randomUUID(), "title", "question", UUID.randomUUID(),
                TaskStatus.READY, UUID.randomUUID(), null, null, Instant.now(), Instant.now(), 2, null, null, UUID.randomUUID());
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

        assertThat(new JdbcInvestigationTaskRepository(jdbcTemplate).updateWithVersion(task, 1)).isTrue();

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).update(sql.capture(), any(Object[].class));
        assertThat(sql.getValue()).contains("row_version = ?").contains("AND row_version = ?");
    }

    @Test
    void leaseLookupOnlyReturnsUnreleasedLease() {
        JdbcTemplate jdbcTemplate = org.mockito.Mockito.mock(JdbcTemplate.class);
        UUID taskId = UUID.randomUUID();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(taskId))).thenReturn(List.of());

        new JdbcTaskLeaseRepository(jdbcTemplate).findActiveByTaskId(taskId);

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sql.capture(), any(RowMapper.class), eq(taskId));
        assertThat(sql.getValue()).contains("released_at IS NULL");
    }

    @Test
    void invocationLookupUsesIdempotencyKey() {
        JdbcTemplate jdbcTemplate = org.mockito.Mockito.mock(JdbcTemplate.class);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenReturn(List.of());

        new JdbcToolInvocationRepository(jdbcTemplate).findByIdempotencyKey("intent-1");

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sql.capture(), any(RowMapper.class), eq("intent-1"));
        assertThat(sql.getValue()).contains("idempotency_key = ?");
    }
}
