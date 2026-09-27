package cn.com.crc.ems.finance.agent.task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cn.com.crc.ems.finance.agent.task.application.TaskIntakeApplicationService;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.web.TaskIntakeController;
import cn.com.crc.ems.finance.agent.task.web.TaskIntakeExceptionHandler;

/** 验证任务发起 REST 契约的必要头和最小响应边界。 */
class TaskIntakeControllerTest {

    private static final UUID ACTOR = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final Instant NOW = Instant.parse("2026-09-27T02:00:00Z");

    @Test
    void createsDraftWithExplicitActorAndIdempotencyHeaders() throws Exception {
        TaskIntakeApplicationService service = mock(TaskIntakeApplicationService.class);
        InvestigationTask task = task();
        when(service.createTask(any())).thenReturn(task);
        MockMvc mvc = mvc(service);

        mvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Actor-User-Id", ACTOR)
                .header("Idempotency-Key", "task-intake-000001")
                .content("{\"title\":\"审批配置调查\",\"originalQuestion\":\"请检查审批配置\","
                        + "\"initiatorUserId\":\"00000000-0000-0000-0000-000000000001\","
                        + "\"runtimeBundleId\":\"00000000-0000-0000-0000-000000000002\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void rejectsShortIdempotencyKeyBeforeCallingService() throws Exception {
        TaskIntakeApplicationService service = mock(TaskIntakeApplicationService.class);

        mvc(service).perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Actor-User-Id", ACTOR)
                .header("Idempotency-Key", "short")
                .content("{\"title\":\"标题\",\"originalQuestion\":\"问题\","
                        + "\"initiatorUserId\":\"00000000-0000-0000-0000-000000000001\","
                        + "\"runtimeBundleId\":\"00000000-0000-0000-0000-000000000002\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsNaturalLanguageReasoningFieldByBeanValidationContract() throws Exception {
        TaskIntakeApplicationService service = mock(TaskIntakeApplicationService.class);

        mvc(service).perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Actor-User-Id", ACTOR)
                .header("Idempotency-Key", "task-intake-000002")
                .content("{\"title\":\"标题\",\"originalQuestion\":\"问题\","
                        + "\"initiatorUserId\":\"00000000-0000-0000-0000-000000000001\","
                        + "\"runtimeBundleId\":\"00000000-0000-0000-0000-000000000002\","
                        + "\"reasoning\":\"forbidden\"}"))
                .andExpect(status().isBadRequest());
    }

    private static MockMvc mvc(TaskIntakeApplicationService service) {
        return MockMvcBuilders.standaloneSetup(new TaskIntakeController(service,
                Clock.fixed(NOW, ZoneOffset.UTC))).setControllerAdvice(new TaskIntakeExceptionHandler()).build();
    }

    private static InvestigationTask task() {
        return new InvestigationTask(UUID.randomUUID(), "审批配置调查", "请检查审批配置", ACTOR, TaskStatus.DRAFT,
                UUID.randomUUID(), null, null, NOW, NOW, 0, null, ACTOR, ACTOR);
    }
}
