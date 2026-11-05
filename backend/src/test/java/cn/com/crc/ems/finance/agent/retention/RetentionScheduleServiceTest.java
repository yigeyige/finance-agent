package cn.com.crc.ems.finance.agent.retention;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ScheduledFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

class RetentionScheduleServiceTest {
    @TempDir Path root;

    @Test
    void updatePersistsTimeAndReplacesPendingExecution() throws Exception {
        RetentionScheduleStore store = new RetentionScheduleStore(root);
        TaskScheduler scheduler = mock(TaskScheduler.class);
        ScheduledFuture<?> old = mock(ScheduledFuture.class);
        ScheduledFuture<?> replacement = mock(ScheduledFuture.class);
        doReturn(old, replacement).when(scheduler).schedule(any(Runnable.class), any(Trigger.class));
        RetentionScheduleService service = new RetentionScheduleService(store, scheduler, () -> { });

        service.start();
        RetentionScheduleService.Job view = service.update(new RetentionScheduleStore.Schedule(1, "03:15"));

        assertThat(RetentionScheduleService.cron(RetentionScheduleStore.DEFAULT)).isEqualTo("0 0 2 * * SUN");
        assertThat(RetentionScheduleService.cron(store.read())).isEqualTo("0 15 3 * * MON");
        assertThat(RetentionScheduleService.cron(new RetentionScheduleStore.Schedule(
                RetentionScheduleStore.Frequency.DAILY, null, null, "01:30"))).isEqualTo("0 30 1 * * *");
        assertThat(RetentionScheduleService.cron(new RetentionScheduleStore.Schedule(
                RetentionScheduleStore.Frequency.MONTHLY, null, 15, "06:20"))).isEqualTo("0 20 6 15 * *");
        assertThat(view.dayOfWeek()).isEqualTo(1);
        assertThat(view.time()).isEqualTo("03:15");
        verify(old).cancel(false);
        ArgumentCaptor<Trigger> triggers = ArgumentCaptor.forClass(Trigger.class);
        verify(scheduler, times(2)).schedule(any(Runnable.class), triggers.capture());
        TriggerContext context = mock(TriggerContext.class);
        when(context.getClock()).thenReturn(Clock.fixed(Instant.parse("2026-11-04T00:00:00Z"), ZoneOffset.UTC));
        assertThat(triggers.getAllValues().get(0).nextExecution(context))
                .isEqualTo(Instant.parse("2026-11-07T18:00:00Z"));
    }
}
