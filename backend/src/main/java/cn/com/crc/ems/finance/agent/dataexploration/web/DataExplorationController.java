package cn.com.crc.ems.finance.agent.dataexploration.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.dataexploration.application.DataExplorationService;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseColumnMetadata;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import jakarta.validation.Valid;

/**
 *
 * @Description: 为平台管理员提供不经大模型的 EMS 只读数据探索接口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-16 15:30:00
 */
@RestController
@RequestMapping("/api/v1/data-exploration")
public class DataExplorationController {
    private final DataExplorationService service;

    public DataExplorationController(DataExplorationService service) { this.service = service; }

    /** 获取指定数据源可浏览的受控表目录，未指定时默认第一个启用实例。 */
    @GetMapping("/catalog")
    public DataExplorationCatalogResponse catalog(@RequestParam(required = false) UUID configId) {
        requireAdmin();
        return DataExplorationCatalogResponse.from(service.catalog(configId));
    }

    /** 获取允许表的字段结构，不读取业务数据。 */
    @GetMapping("/tables/{schema}/{table}/columns")
    public List<EmsDatabaseColumnMetadata> columns(@PathVariable String schema, @PathVariable String table,
            @RequestParam(required = false) UUID configId) {
        requireAdmin();
        return service.columns(configId, schema, table);
    }

    /** 提交单条受限 SELECT 并返回受限结果投影。 */
    @PostMapping("/queries")
    public DataExplorationQueryResponse query(@Valid @RequestBody DataExplorationQueryRequest request) {
        requireAdmin();
        return DataExplorationQueryResponse.from(service.query(request.configId(), request.sql()));
    }

    private static void requireAdmin() {
        if (!IdentityContext.require().hasRole(RoleCode.PLATFORM_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权使用数据探索");
        }
    }
}
