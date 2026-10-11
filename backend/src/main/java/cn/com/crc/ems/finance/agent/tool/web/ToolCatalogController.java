package cn.com.crc.ems.finance.agent.tool.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.com.crc.ems.finance.agent.tool.application.ToolCatalog;
import cn.com.crc.ems.finance.agent.tool.application.ToolCatalogService;

/**
 *
 * @Description: 工具目录只读管理查询入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@RestController
@RequestMapping("/api/v1/tool-catalog")
public final class ToolCatalogController {

    private final ToolCatalogService catalogService;

    public ToolCatalogController(ToolCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public ToolCatalog current() {
        return catalogService.current();
    }
}
