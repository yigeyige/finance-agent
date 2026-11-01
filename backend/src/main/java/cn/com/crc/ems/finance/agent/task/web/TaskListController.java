package cn.com.crc.ems.finance.agent.task.web;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.task.application.TaskListService;
import cn.com.crc.ems.finance.agent.task.application.TaskListRowService;
import cn.com.crc.ems.finance.agent.task.application.TaskDetailService;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 *
 * @Description: 当前身份可见任务的只读列表 REST 入口，返回展示名称而不泄露连接配置。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-01 00:00:00
 */
@RestController
@Validated
@RequestMapping("/api/v1/tasks")
public class TaskListController {
    private final TaskListService taskListService;
    private final TaskListRowService taskListRowService;
    private final TaskDetailService taskDetailService;
    public TaskListController(TaskListService taskListService, TaskListRowService taskListRowService,
            TaskDetailService taskDetailService) { this.taskListService = taskListService; this.taskListRowService = taskListRowService; this.taskDetailService = taskDetailService; }
    /** 返回当前可信身份可见的任务页，平台管理员可查看全部任务。 */
    @GetMapping
    public TaskListRowService.Page list(@RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize,
            @RequestParam(required = false) TaskStatus status, @RequestParam(required = false) @Size(max = 100) String keyword) {
        return taskListRowService.rows(taskListService.list(IdentityContext.require(), page, pageSize, status, keyword));
    }
    /** 返回任务的只读业务、范围与方案详情。 */
    @GetMapping("/{taskId}")
    public TaskDetailService.Detail detail(@PathVariable UUID taskId) { return taskDetailService.find(taskId, IdentityContext.require()); }
}
