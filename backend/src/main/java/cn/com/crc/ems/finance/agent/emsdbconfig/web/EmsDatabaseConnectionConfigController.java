package cn.com.crc.ems.finance.agent.emsdbconfig.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigService;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import jakarta.validation.Valid;

/** 平台管理员维护 EMS 数据库连接的 REST 入口。 */
@RestController
@RequestMapping("/api/v1/ems-database-configurations")
public class EmsDatabaseConnectionConfigController {
    private final EmsDatabaseConnectionConfigService service;
    public EmsDatabaseConnectionConfigController(EmsDatabaseConnectionConfigService service) { this.service = service; }

    @GetMapping public List<EmsDatabaseConnectionConfigResponse> list() { requireAdmin(); return service.list().stream().map(EmsDatabaseConnectionConfigResponse::from).toList(); }
    @PostMapping public ResponseEntity<EmsDatabaseConnectionConfigResponse> create(@Valid @RequestBody EmsDatabaseConnectionConfigRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(EmsDatabaseConnectionConfigResponse.from(service.create(request.name(), request.jdbcUrl(), request.username(), request.password(), request.connectTimeoutSeconds(), request.queryTimeoutSeconds(), request.maxRows(), requireAdmin()))); }
    @PutMapping("/{configId}") public EmsDatabaseConnectionConfigResponse update(@PathVariable UUID configId, @Valid @RequestBody EmsDatabaseConnectionConfigRequest request) { return EmsDatabaseConnectionConfigResponse.from(service.update(configId, request.name(), request.jdbcUrl(), request.username(), request.password(), request.connectTimeoutSeconds(), request.queryTimeoutSeconds(), request.maxRows(), requireAdmin())); }
    @PostMapping("/{configId}/enable") public ResponseEntity<Void> enable(@PathVariable UUID configId) { service.enable(configId, requireAdmin()); return ResponseEntity.noContent().build(); }
    @PostMapping("/{configId}/disable") public ResponseEntity<Void> disable(@PathVariable UUID configId) { service.disable(configId, requireAdmin()); return ResponseEntity.noContent().build(); }
    @PostMapping("/{configId}/test") public ResponseEntity<Void> test(@PathVariable UUID configId) { if (!service.test(configId)) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "EMS 数据库连接测试失败，请检查配置"); return ResponseEntity.noContent().build(); }
    private static UUID requireAdmin() { var identity = IdentityContext.require(); if (!identity.hasRole(RoleCode.PLATFORM_ADMIN)) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权维护 EMS 数据库配置"); return identity.userId(); }
}
