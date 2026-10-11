package cn.com.crc.ems.finance.agent.task.web;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.com.crc.ems.finance.agent.task.application.PublicTaskEvent;
import cn.com.crc.ems.finance.agent.task.application.TaskEventRole;
import cn.com.crc.ems.finance.agent.task.application.TaskEventStreamService;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 *
 * @Description: 提供断线重放 REST 和同源权限过滤的 SSE 任务事件流。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@RestController
@RequestMapping("/api/tasks/{taskId}/events")
public class TaskEventController {

    private final TaskEventStreamService eventStreamService;

    public TaskEventController(TaskEventStreamService eventStreamService) {
        this.eventStreamService = eventStreamService;
    }

    @GetMapping
    public List<PublicTaskEvent> replay(@PathVariable UUID taskId,
            @RequestParam(name = "after", defaultValue = "0") long after,
            @RequestParam(defaultValue = "100") int limit) {
        return eventStreamService.replay(taskId, IdentityContext.require().userId(), currentRole(), after, limit);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable UUID taskId,
            @RequestParam(name = "lastEventId", defaultValue = "0") long lastEventId,
            @RequestParam(defaultValue = "100") int limit) {
        SseEmitter emitter = new SseEmitter(30_000L);
        try {
            for (PublicTaskEvent event : eventStreamService.replay(taskId, IdentityContext.require().userId(), currentRole(), lastEventId, limit)) {
                emitter.send(SseEmitter.event().id(Long.toString(event.sequence())).name(event.eventType()).data(event));
            }
            emitter.complete();
        } catch (IOException | RuntimeException failure) {
            emitter.completeWithError(failure);
        }
        return emitter;
    }

    /** 将当前身份角色映射为事件流过滤角色。 */
    private static TaskEventRole currentRole() {
        return IdentityContext.require().hasRole(RoleCode.OPERATOR) ? TaskEventRole.OPERATOR : TaskEventRole.VIEWER;
    }
}
