package cn.com.crc.ems.finance.agent.retention;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 * @Description: 向平台管理员提供固定清理任务列表和频率、执行时间修改接口。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-05 00:00:00
 */
@RestController
@RequestMapping("/api/v1/scheduled-jobs")
public final class RetentionScheduleController {
    public record ListResponse(List<RetentionScheduleService.Job> items) { }
    public record ScheduleRequest(String frequency, Integer dayOfWeek, Integer dayOfMonth, String time) {
        public ScheduleRequest(Integer dayOfWeek, String time) { this("WEEKLY", dayOfWeek, null, time); }
    }

    private final RetentionScheduleService service;

    public RetentionScheduleController(RetentionScheduleService service) { this.service = service; }

    /**
     * @description: 只返回应用已登记的调度任务，不读取任意文件路径。
     * @return: 固定任务列表及当前调度时间。
     * @throws: IOException 配置读取失败。
     * @author: yigeyige99
     */
    @GetMapping
    public ListResponse list() throws IOException {
        requireAdmin();
        return new ListResponse(List.of(service.current()));
    }

    /**
     * @description: 校验管理员身份、频率及时间后修改清理任务计划。
     * @param: request 新的频率和执行时间。
     * @return: 持久保存后的任务配置。
     * @throws: IOException 配置写入失败。
     * @author: yigeyige99
     */
    @PutMapping("/retention-cleanup/schedule")
    public RetentionScheduleService.Job update(@RequestBody ScheduleRequest request) throws IOException {
        requireAdmin();
        if (request == null || request.frequency() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请选择有效的执行频率和时间");
        }
        try {
            RetentionScheduleStore.Frequency frequency = RetentionScheduleStore.Frequency.valueOf(request.frequency());
            return service.update(new RetentionScheduleStore.Schedule(
                    frequency, request.dayOfWeek(), request.dayOfMonth(), request.time()));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请选择有效的执行频率和时间");
        }
    }

    /**
     * @description: 平台管理员异步提交一次固定清理任务；重复手动提交返回冲突，不更改 cron。
     * @return: 已接受的 HTTP 状态。
     * @author: yigeyige99
     */
    @PostMapping("/retention-cleanup/runs")
    public ResponseEntity<Void> runOnce() {
        requireAdmin();
        if (!service.runOnce()) throw new ResponseStatusException(HttpStatus.CONFLICT, "清理任务已提交，请等待完成");
        return ResponseEntity.accepted().build();
    }

    private static void requireAdmin() {
        if (!IdentityContext.require().hasRole(RoleCode.PLATFORM_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权管理调度任务");
        }
    }
}
