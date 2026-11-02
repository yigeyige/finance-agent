package cn.com.crc.ems.finance.agent.diagnostics;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 * @Description: 平台管理员读取当前进程最近一次异常快照的只读接口。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-02 23:00:00
 */
@RestController
@RequestMapping("/api/v1/latest-exception")
public final class LatestExceptionController {
    private final LatestExceptionStore store;

    public LatestExceptionController(LatestExceptionStore store) {
        this.store = store;
    }

    /** 校验可信身份后读取快照，未发生异常时明确返回空项。 */
    @GetMapping
    public Response latest() {
        if (!IdentityContext.require().hasRole(RoleCode.PLATFORM_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权查看异常堆栈");
        }
        return new Response(store.latest());
    }

    public record Response(LatestExceptionStore.Snapshot item) { }
}
