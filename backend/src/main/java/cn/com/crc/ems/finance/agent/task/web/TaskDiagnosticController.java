package cn.com.crc.ems.finance.agent.task.web;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.task.application.TaskDiagnosticService;

/** 为 OPERATOR/VIEWER 提供只读任务诊断摘要。 */
@RestController
@RequestMapping("/api/v1/tasks/{taskId}/diagnostics")
public final class TaskDiagnosticController {

    private final TaskDiagnosticService diagnosticService;

    public TaskDiagnosticController(TaskDiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    /** 查询脱敏诊断；不提供任何改变任务状态的动作。 */
    @GetMapping
    public TaskDiagnosticResponse diagnose(@PathVariable UUID taskId) {
        var identity = IdentityContext.require();
        if (!identity.hasRole(RoleCode.OPERATOR) && !identity.hasRole(RoleCode.VIEWER)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权查看任务诊断");
        }
        return TaskDiagnosticResponse.from(diagnosticService.diagnose(taskId));
    }
}
