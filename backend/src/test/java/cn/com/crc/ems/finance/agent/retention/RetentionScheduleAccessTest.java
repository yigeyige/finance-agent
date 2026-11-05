package cn.com.crc.ems.finance.agent.retention;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ScheduledFuture;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.iam.web.PermissionGuardInterceptor;

class RetentionScheduleAccessTest {
    @TempDir Path root;

    @AfterEach void clear() { IdentityContext.clear(); }

    @Test
    void administratorCanUpdateButOperatorAndInvalidTimeAreRejected() throws Exception {
        TaskScheduler scheduler = mock(TaskScheduler.class);
        when(scheduler.schedule(any(Runnable.class), any(Trigger.class))).thenReturn(mock(ScheduledFuture.class));
        RetentionScheduleService service = new RetentionScheduleService(
                new RetentionScheduleStore(root), scheduler, () -> { });
        RetentionScheduleController controller = new RetentionScheduleController(service);
        IdentityContext.set(identity(RoleCode.PLATFORM_USER));
        assertThatThrownBy(controller::list).isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode())
                        .isEqualTo(HttpStatus.FORBIDDEN));
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(new PermissionGuardInterceptor().preHandle(
                new MockHttpServletRequest("PUT", "/api/v1/scheduled-jobs/retention-cleanup/schedule"),
                response, null)).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);

        IdentityContext.set(identity(RoleCode.PLATFORM_ADMIN));
        assertThat(controller.list().items()).hasSize(1);
        assertThatThrownBy(() -> controller.update(new RetentionScheduleController.ScheduleRequest(9, "02:00")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
        assertThat(controller.update(new RetentionScheduleController.ScheduleRequest(2, "08:30")).time())
                .isEqualTo("08:30");
        assertThat(controller.update(new RetentionScheduleController.ScheduleRequest(
                "DAILY", null, null, "01:30")).frequency()).isEqualTo("DAILY");
        assertThat(controller.update(new RetentionScheduleController.ScheduleRequest(
                "MONTHLY", null, 15, "06:20")).dayOfMonth()).isEqualTo(15);
        assertThatThrownBy(() -> controller.update(new RetentionScheduleController.ScheduleRequest(
                "MONTHLY", null, 29, "06:20"))).isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void administratorCanQueueOneManualRunWithoutChangingSchedule() throws Exception {
        TaskScheduler scheduler = mock(TaskScheduler.class);
        AtomicReference<Runnable> queued = new AtomicReference<>();
        AtomicInteger executions = new AtomicInteger();
        doAnswer(invocation -> { queued.set(invocation.getArgument(0)); return mock(ScheduledFuture.class); })
                .when(scheduler).schedule(any(Runnable.class), any(Instant.class));
        RetentionScheduleService service = new RetentionScheduleService(
                new RetentionScheduleStore(root), scheduler, executions::incrementAndGet);
        RetentionScheduleController controller = new RetentionScheduleController(service);

        IdentityContext.set(identity(RoleCode.PLATFORM_USER));
        assertThatThrownBy(controller::runOnce).isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode())
                        .isEqualTo(HttpStatus.FORBIDDEN));
        assertThat(queued.get()).isNull();
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(new PermissionGuardInterceptor().preHandle(
                new MockHttpServletRequest("POST", "/api/v1/scheduled-jobs/retention-cleanup/runs"),
                response, null)).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);

        IdentityContext.set(identity(RoleCode.PLATFORM_ADMIN));
        assertThat(controller.runOnce().getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(queued.get()).isNotNull();
        assertThat(executions).hasValue(0);
        assertThatThrownBy(controller::runOnce).isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT));
        queued.get().run();
        assertThat(executions).hasValue(1);
        assertThat(controller.runOnce().getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(service.current().time()).isEqualTo("02:00");
    }

    private static CurrentIdentity identity(RoleCode role) {
        return new CurrentIdentity(UUID.randomUUID(), "test", "测试用户", Set.of(role));
    }
}
