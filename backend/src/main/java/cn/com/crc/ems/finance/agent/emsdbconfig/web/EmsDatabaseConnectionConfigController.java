package cn.com.crc.ems.finance.agent.emsdbconfig.web;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigService;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsSchemaCatalog;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsSchemaCatalogService;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsSchemaCatalogRepository;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsSchemaSyncException;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import jakarta.validation.Valid;

/**
 *
 * @Description: 平台管理员维护 EMS 数据库连接的 REST 入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@RestController
@RequestMapping("/api/v1/ems-database-configurations")
public class EmsDatabaseConnectionConfigController {
    private final EmsDatabaseConnectionConfigService service;
    private final EmsSchemaCatalogService schemaService;
    public EmsDatabaseConnectionConfigController(EmsDatabaseConnectionConfigService service,
            EmsSchemaCatalogService schemaService) { this.service = service; this.schemaService = schemaService; }

    @GetMapping public List<EmsDatabaseConnectionConfigResponse> list() { requireAdmin(); return service.list().stream().map(this::response).toList(); }
    @GetMapping("/enabled") public List<EmsDatabaseInstanceResponse> listEnabled() { IdentityContext.require(); return service.listEnabled().stream().map(EmsDatabaseInstanceResponse::from).toList(); }
    @PostMapping public ResponseEntity<EmsDatabaseConnectionConfigResponse> create(@Valid @RequestBody EmsDatabaseConnectionConfigRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(response(service.create(request.name(), request.jdbcUrl(), request.username(), request.password(), request.connectTimeoutSeconds(), request.queryTimeoutSeconds(), request.maxRows(), requireAdmin()))); }
    @PutMapping("/{configId}") public EmsDatabaseConnectionConfigResponse update(@PathVariable UUID configId, @Valid @RequestBody EmsDatabaseConnectionConfigRequest request) { return response(service.update(configId, request.name(), request.jdbcUrl(), request.username(), request.password(), request.connectTimeoutSeconds(), request.queryTimeoutSeconds(), request.maxRows(), requireAdmin())); }
    @PostMapping("/{configId}/enable") public ResponseEntity<Void> enable(@PathVariable UUID configId) { service.enable(configId, requireAdmin()); return ResponseEntity.noContent().build(); }
    @PostMapping("/{configId}/disable") public ResponseEntity<Void> disable(@PathVariable UUID configId) { service.disable(configId, requireAdmin()); return ResponseEntity.noContent().build(); }
    @PostMapping("/{configId}/test") public ResponseEntity<Void> test(@PathVariable UUID configId) { if (!service.test(configId)) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "EMS 数据库连接测试失败，请检查配置"); return ResponseEntity.noContent().build(); }
    /** 管理员手动刷新实例结构目录，失败时保留上次成功快照。 */
    @PostMapping("/{configId}/sync-schema") public EmsSchemaCatalogRepository.SyncStatus syncSchema(@PathVariable UUID configId) { requireAdmin(); return schemaService.sync(configId); }

    /**
     * @description: 仅向管理员返回指定实例已保存的表字段目录，从未同步时返回 404。
     * @param: configId 数据库实例标识
     * @return: 只含表字段元数据的目录快照
     * @author: yigeyige99
     */
    @GetMapping("/{configId}/schema-catalog")
    public ResponseEntity<EmsSchemaCatalog> schemaCatalog(@PathVariable UUID configId) {
        requireAdmin();
        return ResponseEntity.of(schemaService.view(configId));
    }
    @ExceptionHandler(EmsSchemaSyncException.class)
    public ResponseEntity<Map<String, String>> syncFailed(EmsSchemaSyncException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "SCHEMA_SYNC_FAILED",
                "message", "数据库实例结构同步失败，原目录已保留"));
    }
    private EmsDatabaseConnectionConfigResponse response(cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig value) {
        return EmsDatabaseConnectionConfigResponse.from(value, schemaService.status(value.configId()).orElse(null));
    }
    private static UUID requireAdmin() { var identity = IdentityContext.require(); if (!identity.hasRole(RoleCode.PLATFORM_ADMIN)) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权维护 EMS 数据库配置"); return identity.userId(); }
}
