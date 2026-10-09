package cn.com.crc.ems.finance.agent.modelconfig.web;

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
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.modelconfig.application.ModelConnectionConfigService;
import jakarta.validation.Valid;

/** 平台管理员维护大模型连接的 REST 入口。 */
@RestController
@RequestMapping("/api/v1/model-configurations")
public class ModelConnectionConfigController {
    private final ModelConnectionConfigService service;
    public ModelConnectionConfigController(ModelConnectionConfigService service) { this.service = service; }
    @GetMapping public List<ModelConnectionConfigResponse> list() { requireAdmin(); return service.list().stream().map(ModelConnectionConfigResponse::from).toList(); }
    @PostMapping public ResponseEntity<ModelConnectionConfigResponse> create(@Valid @RequestBody ModelConnectionConfigRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(ModelConnectionConfigResponse.from(service.create(request.name(), request.provider(), request.apiUrl(), request.modelName(), request.reasoningEffort(), request.apiKey(), requireAdmin()))); }
    @PutMapping("/{configId}") public ModelConnectionConfigResponse update(@PathVariable UUID configId, @Valid @RequestBody ModelConnectionConfigRequest request) { return ModelConnectionConfigResponse.from(service.update(configId, request.name(), request.provider(), request.apiUrl(), request.modelName(), request.reasoningEffort(), request.apiKey(), requireAdmin())); }
    @PostMapping("/{configId}/enable") public ResponseEntity<Void> enable(@PathVariable UUID configId) { service.enable(configId, requireAdmin()); return ResponseEntity.noContent().build(); }
    private static UUID requireAdmin() { var identity = IdentityContext.require(); if (!identity.hasRole(RoleCode.PLATFORM_ADMIN)) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权维护大模型配置"); return identity.userId(); }
}
