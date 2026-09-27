package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskLeaseService;
import cn.com.crc.ems.finance.agent.task.infrastructure.worker.PostgresWorker;
import cn.com.crc.ems.finance.agent.task.infrastructure.worker.WorkerStepHandler;

class PostgresWorkerTest {

    @Test
    void workerDoesNotClaimBeforeStartOrAfterGracefulStop() {
        TaskLeaseService leaseService = mock(TaskLeaseService.class);
        WorkerStepHandler handler = claim -> {
        };
        FinanceAgentProperties.Worker properties = new FinanceAgentProperties.Worker();
        PostgresWorker worker = new PostgresWorker(leaseService, handler, properties);

        worker.runOnce();
        assertThat(worker.isRunning()).isFalse();
        worker.start();
        assertThat(worker.isRunning()).isTrue();
        worker.stop();
        clearInvocations(leaseService);
        worker.runOnce();
        assertThat(worker.isRunning()).isFalse();
        verifyNoInteractions(leaseService);
    }
}
