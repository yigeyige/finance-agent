package cn.com.crc.ems.finance.agent.task.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.task.application.TaskListService;
import cn.com.crc.ems.finance.agent.task.application.TaskDetailService;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/** 当前身份可见任务的只读列表 REST 入口。 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskListController {
    private final TaskListService taskListService;
    private final TaskDetailService taskDetailService;
    public TaskListController(TaskListService taskListService, TaskDetailService taskDetailService) { this.taskListService = taskListService; this.taskDetailService = taskDetailService; }
    /** 返回当前可信身份可见的最近任务，平台管理员可查看全部任务。 */
    @GetMapping
    public List<Item> list() { return taskListService.listRecent(IdentityContext.require()).stream().map(Item::from).toList(); }
    /** 返回任务的只读业务、范围与方案详情。 */
    @GetMapping("/{taskId}")
    public TaskDetailService.Detail detail(@PathVariable UUID taskId) { return taskDetailService.find(taskId, IdentityContext.require()); }
    public record Item(UUID taskId, String title, String status, Instant createdAt) {
        static Item from(InvestigationTask task) { return new Item(task.taskId(), task.title(), task.status().name(), task.createdAt()); }
    }
}
