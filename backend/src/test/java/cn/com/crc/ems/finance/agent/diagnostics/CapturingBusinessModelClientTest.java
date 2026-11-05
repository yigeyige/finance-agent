package cn.com.crc.ems.finance.agent.diagnostics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.shared.web.GlobalApiExceptionHandler;

/**
 * @Description: 验证模型端口仅在最终异常向外抛出时覆盖最近堆栈。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-02 23:00:00
 */
class CapturingBusinessModelClientTest {
    @Test
    void attributesBackgroundModelFailureToTaskInitiator() {
        UUID taskId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        var context = new ModelPlanContext(taskId, "任务", "说明", UUID.randomUUID(), "{}");
        BusinessModelClient delegate = mock(BusinessModelClient.class);
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        when(tasks.findById(taskId)).thenReturn(Optional.of(new InvestigationTask(taskId, "任务", "说明", owner,
                TaskStatus.DRAFT, UUID.randomUUID(), null, null, null, Instant.now(), Instant.now(), 0,
                null, owner, owner)));
        when(delegate.generatePlan(context)).thenThrow(new IllegalStateException("模型失败"));
        var store = new LatestExceptionStore(Clock.systemUTC());
        var client = new CapturingBusinessModelClient(delegate, store, tasks);
        assertThatThrownBy(() -> client.generatePlan(context)).isInstanceOf(IllegalStateException.class);
        assertThat(store.latestFor(owner)).isNotNull();
    }
    @Test
    void capturesAnyPropagatedModelFailureAndLeavesSuccessfulCallsAlone() {
        BusinessModelClient delegate = mock(BusinessModelClient.class);
        LatestExceptionStore store = new LatestExceptionStore(Clock.systemUTC());
        CapturingBusinessModelClient client = new CapturingBusinessModelClient(delegate, store);
        RuntimeException failure = new IllegalArgumentException("model raw response");
        when(delegate.generatePlan(null)).thenThrow(failure);

        assertThatThrownBy(() -> client.generatePlan(null)).isSameAs(failure);
        assertThat(store.latest().source()).isEqualTo(LatestExceptionStore.Source.MODEL_INTERACTION);
        assertThat(store.latest().stackTrace()).doesNotContain("model raw response");
        new GlobalApiExceptionHandler(store).unknown(failure);
        assertThat(store.latest().source()).isEqualTo(LatestExceptionStore.Source.MODEL_INTERACTION);

        @SuppressWarnings("unchecked") BusinessModelResult<ModelPlanSuggestion> result = mock(BusinessModelResult.class);
        doReturn(result).when(delegate).generatePlan(null);
        var previous = store.latest();
        assertThat(client.generatePlan(null)).isSameAs(result);
        assertThat(store.latest()).isSameAs(previous);
    }
}
