package cn.com.crc.ems.finance.agent.task.web;

import java.time.Clock;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import cn.com.crc.ems.finance.agent.shared.domain.UtcTime;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.task.application.AddTaskPremiseCommand;
import cn.com.crc.ems.finance.agent.task.application.ConfirmScopeCommand;
import cn.com.crc.ems.finance.agent.task.application.CreateTaskDraftCommand;
import cn.com.crc.ems.finance.agent.task.application.ScopeConfirmationResult;
import cn.com.crc.ems.finance.agent.task.application.TaskIntakeApplicationService;

/** 任务草稿、运维前提和范围确认 REST 入口。 */
@RestController
@RequestMapping("/api/v1/tasks")
public final class TaskIntakeController {

    private final TaskIntakeApplicationService intakeService;
    private final Clock applicationClock;

    public TaskIntakeController(TaskIntakeApplicationService intakeService, Clock applicationClock) {
        this.intakeService = intakeService;
        this.applicationClock = applicationClock;
    }

    @PostMapping
    public ResponseEntity<TaskIntakeResponse> createTask(@Valid @RequestBody CreateTaskRequest request,
            @RequestHeader(value = "X-Actor-User-Id", required = false) UUID actorUserId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        actorUserId = trustedActor(actorUserId);
        var task = intakeService.createTask(new CreateTaskDraftCommand(request.title(), request.originalQuestion(),
                request.initiatorUserId(), request.runtimeBundleId(), actorUserId, UtcTime.now(applicationClock)));
        return ResponseEntity.created(UriComponentsBuilder.fromPath("/api/v1/tasks/{taskId}")
                .buildAndExpand(task.taskId()).toUri()).body(TaskIntakeResponse.task(task));
    }

    @PostMapping("/{taskId}/premises")
    public ResponseEntity<TaskIntakeResponse> addPremise(@PathVariable UUID taskId,
            @Valid @RequestBody AddPremiseRequest request, @RequestHeader(value = "X-Actor-User-Id", required = false) UUID actorUserId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        actorUserId = trustedActor(actorUserId);
        var premise = intakeService.addPremise(new AddTaskPremiseCommand(taskId, request.premiseType(),
                request.subjectKey(), request.statement(), actorUserId, request.sourceDescription(),
                UtcTime.now(applicationClock)));
        var task = intakeService.findTask(taskId);
        return ResponseEntity.status(201).body(TaskIntakeResponse.premise(task, premise));
    }

    @PostMapping("/{taskId}/scope-confirmations")
    public ResponseEntity<TaskIntakeResponse> confirmScope(@PathVariable UUID taskId,
            @Valid @RequestBody ConfirmScopeRequest request, @RequestHeader(value = "X-Actor-User-Id", required = false) UUID actorUserId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        actorUserId = trustedActor(actorUserId);
        ScopeConfirmationResult result = intakeService.confirmScope(new ConfirmScopeCommand(taskId,
                request.systemCode(), request.objectSelector().toString(), request.effectiveTimeRange().toString(),
                request.includedPaths().toString(), request.excludedPaths().toString(),
                request.allowedReadFields().toString(), request.allowedRetainFields().toString(),
                request.allowedModelFields().toString(), request.schemaVersion(), actorUserId,
                UtcTime.now(applicationClock)));
        return ResponseEntity.status(201).body(TaskIntakeResponse.scope(result.task(), result.scopeRevision()));
    }

    private static void requireIdempotencyKey(String value) {
        if (value == null || value.isBlank() || value.length() < 16 || value.length() > 200) {
            throw new IllegalArgumentException("Idempotency-Key must contain 16 to 200 characters");
        }
    }

    /** 优先使用安全上下文，保留独立控制器测试的旧头兼容路径。 */
    private static UUID trustedActor(UUID legacyActorUserId) {
        return IdentityContext.current() == null ? legacyActorUserId : IdentityContext.require().userId();
    }
}
