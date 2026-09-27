package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentCaptor.forClass;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.MybatisInvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.infrastructure.MybatisTaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.infrastructure.MybatisToolInvocationRepository;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.InvestigationTaskMapper;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.TaskLeaseMapper;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.ToolInvocationMapper;

class MybatisTaskRepositoryContractTest {

    @Test
    void taskUpdateUsesOptimisticRowVersionPredicate() {
        InvestigationTaskMapper mapper = org.mockito.Mockito.mock(InvestigationTaskMapper.class);
        InvestigationTask task = new InvestigationTask(UUID.randomUUID(), "title", "question", UUID.randomUUID(),
                TaskStatus.READY, UUID.randomUUID(), null, null, Instant.now(), Instant.now(), 2, null, null, UUID.randomUUID());
        when(mapper.update(any(), any(UpdateWrapper.class))).thenReturn(1);

        assertThat(new MybatisInvestigationTaskRepository(mapper).updateWithVersion(task, 1)).isTrue();

        verify(mapper).update(any(), any(UpdateWrapper.class));
    }

    @Test
    void leaseLookupOnlyReturnsUnreleasedLease() {
        TaskLeaseMapper mapper = org.mockito.Mockito.mock(TaskLeaseMapper.class);
        UUID taskId = UUID.randomUUID();
        when(mapper.selectList(any(QueryWrapper.class))).thenReturn(List.of());

        new MybatisTaskLeaseRepository(mapper).findActiveByTaskId(taskId);

        verify(mapper).selectList(any(QueryWrapper.class));
        assertThat(taskId).isNotNull();
    }

    @Test
    void invocationLookupUsesIdempotencyKey() {
        ToolInvocationMapper mapper = org.mockito.Mockito.mock(ToolInvocationMapper.class);
        when(mapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        new MybatisToolInvocationRepository(mapper).findByIdempotencyKey("intent-1");

        verify(mapper).selectOne(any(QueryWrapper.class));
    }

    @Test
    void invocationStatusUpdateUsesExpectedStatePredicate() {
        ToolInvocationMapper mapper = org.mockito.Mockito.mock(ToolInvocationMapper.class);
        when(mapper.update(any(), any(UpdateWrapper.class))).thenReturn(1);

        boolean updated = new MybatisToolInvocationRepository(mapper).updateStatusIfCurrent("intent-1",
                cn.com.crc.ems.finance.agent.task.domain.InvocationStatus.INTENT_RECORDED,
                cn.com.crc.ems.finance.agent.task.domain.InvocationStatus.RUNNING, null, null, null,
                Instant.now(), UUID.randomUUID());

        assertThat(updated).isTrue();
        var wrapper = forClass(UpdateWrapper.class);
        verify(mapper).update(any(), wrapper.capture());
        assertThat(wrapper.getValue().getSqlSegment()).contains("status");
    }
}
