package cn.com.crc.ems.finance.agent.task.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.task.application.TaskListService;
import cn.com.crc.ems.finance.agent.task.application.TaskDetailService;
import cn.com.crc.ems.finance.agent.task.application.TaskPage;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 *
 * @Description: 当前身份可见任务的只读列表 REST 入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@RestController
@Validated
@RequestMapping("/api/v1/tasks")
public class TaskListController {
    private final TaskListService taskListService;
    private final TaskDetailService taskDetailService;
    public TaskListController(TaskListService taskListService, TaskDetailService taskDetailService) { this.taskListService = taskListService; this.taskDetailService = taskDetailService; }
    /** 返回当前可信身份可见的任务页，平台管理员可查看全部任务。 */
    @GetMapping
    public PageResponse list(@RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize,
            @RequestParam(required = false) TaskStatus status, @RequestParam(required = false) @Size(max = 100) String keyword) {
        return PageResponse.from(taskListService.list(IdentityContext.require(), page, pageSize, status, keyword));
    }
    /** 返回任务的只读业务、范围与方案详情。 */
    @GetMapping("/{taskId}")
    public TaskDetailService.Detail detail(@PathVariable UUID taskId) { return taskDetailService.find(taskId, IdentityContext.require()); }
    public record Item(UUID taskId, String title, String status, UUID initiatorUserId, Instant createdAt) {
        static Item from(InvestigationTask task) { return new Item(task.taskId(), task.title(), task.status().name(), task.initiatorUserId(), task.createdAt()); }
    }
    public record PageResponse(List<Item> items, long total, int page, int pageSize) {
        static PageResponse from(TaskPage value) {
            return new PageResponse(value.items().stream().map(Item::from).toList(), value.total(), value.page(),
                    value.pageSize());
        }
    }
}
