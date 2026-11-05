package cn.com.crc.ems.finance.agent.retention;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 * @Description: 仅向平台管理员返回有界的清理运行日志，不接受文件路径参数。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 20:00:00
 */
@RestController
@RequestMapping("/api/v1/retention-runs")
public final class RetentionLogController {
    public record Response(List<RetentionRunLog.Entry> items) { }

    private final RetentionRunLog log;

    public RetentionLogController(RetentionRunLog log) { this.log = log; }

    /**
     * @description: 校验可信管理员身份后读取最近 50 次清理结果。
     * @return: 只读的最近运行列表。
     * @throws: IOException 清理日志读取失败。
     * @author: yigeyige99
     */
    @GetMapping
    public Response recent() throws IOException {
        if (!IdentityContext.require().hasRole(RoleCode.PLATFORM_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权查看清理日志");
        }
        return new Response(log.recent());
    }
}
