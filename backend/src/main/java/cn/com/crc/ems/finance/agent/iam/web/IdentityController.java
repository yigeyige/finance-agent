package cn.com.crc.ems.finance.agent.iam.web;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;

/** 提供当前身份和前端导航权限查询。 */
@RestController
@RequestMapping("/api/v1/identity")
public final class IdentityController {

    private final Environment environment;

    public IdentityController(Environment environment) {
        this.environment = environment;
    }

    /** 返回当前可信身份，未认证时返回拒绝。 */
    @GetMapping("/me")
    public ResponseEntity<CurrentIdentityResponse> current() {
        var identity = IdentityContext.current();
        if (identity == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(CurrentIdentityResponse.from(identity, !environment.acceptsProfiles("prod")));
    }
}
