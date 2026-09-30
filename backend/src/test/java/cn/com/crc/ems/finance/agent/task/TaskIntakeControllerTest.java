package cn.com.crc.ems.finance.agent.task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import cn.com.crc.ems.finance.agent.task.application.ScopeConfirmationResult;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;
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

    @Test
    void confirmsScopeWithNestedJsonFields() throws Exception {
        TaskIntakeApplicationService service = mock(TaskIntakeApplicationService.class);
        UUID taskId = UUID.randomUUID();
        UUID scopeId = UUID.randomUUID();
        InvestigationTask task = new InvestigationTask(taskId, "审批配置调查", "请检查审批配置", ACTOR,
                TaskStatus.READY, UUID.randomUUID(), scopeId, null, NOW, NOW, 2, null, ACTOR, ACTOR);
        ScopeRevision revision = new ScopeRevision(scopeId, taskId, 1, "FIN-SYN",
                new VersionedJson("scope-v1", "{\"employeeKey\":\"EMP-SYN-001\"}"),
                new VersionedJson("scope-v1", "{\"from\":\"2026-01-01T00:00:00Z\"}"),
                new VersionedJson("scope-v1", "[\"workflow.reference\"]"),
                new VersionedJson("scope-v1", "[\"credential.secret\"]"),
                new VersionedJson("scope-v1", "[\"reference.id\"]"),
                new VersionedJson("scope-v1", "[\"reference.id\"]"),
                new VersionedJson("scope-v1", "[\"reference.id\"]"), ACTOR, NOW, "a".repeat(64),
                "scope-v1", ACTOR, NOW, ACTOR, NOW);
        when(service.confirmScope(any())).thenReturn(new ScopeConfirmationResult(task, revision));

        mvc(service).perform(post("/api/v1/tasks/{taskId}/scope-confirmations", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Actor-User-Id", ACTOR)
                .header("Idempotency-Key", "scope-confirmation-0001")
                .content("{\"systemCode\":\"FIN-SYN\",\"objectSelector\":{\"employeeKey\":\"EMP-SYN-001\"},"
                        + "\"effectiveTimeRange\":{\"from\":\"2026-01-01T00:00:00Z\"},"
                        + "\"includedPaths\":[\"workflow.reference\"],\"excludedPaths\":[\"credential.secret\"],"
                        + "\"allowedReadFields\":[\"reference.id\"],\"allowedRetainFields\":[\"reference.id\"],"
                        + "\"allowedModelFields\":[\"reference.id\"],\"schemaVersion\":\"scope-v1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currentScopeRevisionId").value(scopeId.toString()))
                .andExpect(jsonPath("$.scopeRevisionNo").value(1));
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
