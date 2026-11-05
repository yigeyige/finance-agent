package cn.com.crc.ems.finance.agent.iam.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.application.PlatformUserManagementService;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUserStatus;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @Description: 管理员用户管理接口，只返回非敏感账户字段。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-05 00:00:00
 */
@RestController
@RequestMapping("/api/v1/platform-users")
public final class PlatformUserController {
    private final PlatformUserManagementService service;

    public PlatformUserController(PlatformUserManagementService service) { this.service = service; }

    /** 列出包含停用账号的用户清单。 */
    @GetMapping
    public List<UserResponse> list() {
        return service.list(IdentityContext.require()).stream().map(UserResponse::from).toList();
    }

    /** 新增用户及初始密码。 */
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(service.create(IdentityContext.require(),
                request.authSubject(), request.employeeNo(), request.displayName(), request.role(), request.password())));
    }

    /** 修改显示名或角色，账号和工号保持不变。 */
    @PutMapping("/{userId}")
    public UserResponse update(@PathVariable UUID userId, @Valid @RequestBody UpdateRequest request) {
        return UserResponse.from(service.update(IdentityContext.require(), userId, request.displayName(), request.role()));
    }

    /** 将删除映射为停用，不删业务审计数据。 */
    @DeleteMapping("/{userId}")
    public UserResponse disable(@PathVariable UUID userId) {
        return UserResponse.from(service.disable(IdentityContext.require(), userId));
    }

    /** 重新启用停用账号。 */
    @PostMapping("/{userId}/enable")
    public UserResponse enable(@PathVariable UUID userId) {
        return UserResponse.from(service.enable(IdentityContext.require(), userId));
    }

    /** 设置新密码，接口不回传凭据。 */
    @PostMapping("/{userId}/password")
    public ResponseEntity<Void> resetPassword(@PathVariable UUID userId, @Valid @RequestBody PasswordRequest request) {
        service.resetPassword(IdentityContext.require(), userId, request.password());
        return ResponseEntity.noContent().build();
    }

    public record CreateRequest(@NotBlank @Size(max = 200) String authSubject,
            @NotBlank @Size(max = 100) String employeeNo,
            @NotBlank @Size(max = 200) String displayName,
            @NotNull RoleCode role,
            @NotBlank @Size(min = 8, max = 128) String password) { }
    public record UpdateRequest(@NotBlank @Size(max = 200) String displayName, @NotNull RoleCode role) { }
    public record PasswordRequest(@NotBlank @Size(min = 8, max = 128) String password) { }

    public record UserResponse(UUID userId, String authSubject, String employeeNo, String displayName,
            AgentUserStatus status, RoleCode role, Instant createdAt) {
        public static UserResponse from(PlatformUserManagementService.ManagedUser managed) {
            var user = managed.user();
            return new UserResponse(user.userId(), user.authSubject(), user.employeeNo(), user.displayName(),
                    user.status(), managed.role(), user.createdAt());
        }
    }
}
