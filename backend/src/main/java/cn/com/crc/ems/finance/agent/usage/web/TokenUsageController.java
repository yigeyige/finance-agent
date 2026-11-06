package cn.com.crc.ems.finance.agent.usage.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.usage.application.TokenUsageQueryService;

/**
 * @Description: 模型 token 用量只读入口，身份由权限拦截器保证，角色差异在查询层体现。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:10:00
 */
@RestController
@RequestMapping("/api/v1/usage")
public class TokenUsageController {

    private final TokenUsageQueryService service;

    public TokenUsageController(TokenUsageQueryService service) {
        this.service = service;
    }

    /** 返回最近 30 个自然日的 token 用量；普通用户为本人，平台管理员为全平台。 */
    @GetMapping("/tokens")
    public TokenUsageResponse tokens() {
        return TokenUsageResponse.from(service.summary(IdentityContext.require()));
    }
}
