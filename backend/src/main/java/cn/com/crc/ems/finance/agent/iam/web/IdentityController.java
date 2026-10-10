package cn.com.crc.ems.finance.agent.iam.web;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.application.PasswordLoginService;

/**
 *
 * @Description: 提供当前身份、账号密码登录及退出接口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-10 00:00:00
 */
@RestController
@RequestMapping("/api/v1/identity")
public final class IdentityController {

    private final Environment environment;
    private final PasswordLoginService passwordLoginService;

    public IdentityController(Environment environment, PasswordLoginService passwordLoginService) {
        this.environment = environment;
        this.passwordLoginService = passwordLoginService;
    }

    /**
     *
     * @description: 返回当前可信身份，未认证时返回拒绝。
     *
     * @return: 当前身份或未认证状态
     * @author: yigeyige99
     */
    @GetMapping("/me")
    public ResponseEntity<CurrentIdentityResponse> current() {
        var identity = IdentityContext.current();
        if (identity == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(CurrentIdentityResponse.from(identity, !environment.acceptsProfiles("prod")));
    }

    /**
     *
     * @description: 校验账号密码并建立 HttpOnly 会话。
     *
     * @param: request 登录请求
     * @param: servletRequest 当前 HTTP 请求
     * @param: servletResponse 当前 HTTP 响应
     * @return: 登录后的当前身份或统一的未认证状态
     * @author: yigeyige99
     */
    @PostMapping("/login")
    public ResponseEntity<CurrentIdentityResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        return passwordLoginService.login(request.username().trim().toLowerCase(), request.password()).map(login -> {
            servletResponse.addHeader("Set-Cookie", cookie(login.token(), servletRequest.isSecure(), 28800).toString());
            return ResponseEntity.ok(CurrentIdentityResponse.from(login.identity(), false));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     *
     * @description: 清除当前会话并使浏览器 Cookie 立即失效。
     *
     * @param: request 当前 HTTP 请求
     * @param: response 当前 HTTP 响应
     * @return: 无内容的成功响应
     * @author: yigeyige99
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (PasswordLoginService.COOKIE_NAME.equals(cookie.getName())) {
                    passwordLoginService.logout(cookie.getValue());
                }
            }
        }
        response.addHeader("Set-Cookie", cookie("", request.isSecure(), 0).toString());
        return ResponseEntity.noContent().build();
    }

    private static ResponseCookie cookie(String value, boolean secure, long age) {
        return ResponseCookie.from(PasswordLoginService.COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/api")
                .maxAge(age)
                .build();
    }

    public record LoginRequest(@NotBlank @Size(max = 100) String username, @NotBlank @Size(max = 128) String password) {
    }
}
