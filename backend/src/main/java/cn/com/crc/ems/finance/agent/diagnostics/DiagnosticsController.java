package cn.com.crc.ems.finance.agent.diagnostics;

import java.util.Map;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/diagnostics")
@ConditionalOnProperty(prefix = "finance-agent.diagnostics", name = "api-enabled", havingValue = "true")
/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:35
 */
public final class DiagnosticsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiagnosticsController.class);

    private final DiagnosticHealthService healthService;

    @Autowired
    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: healthService 输入参数。
     * @author: yigeyige99
     */
    public DiagnosticsController(DiagnosticHealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    /**
     *
     * @description: 读取当前配置或状态值。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public DiagnosticsResponse getDiagnostics() {
        DiagnosticHealthService.DiagnosticSnapshot snapshot = healthService.snapshot();
        Map<String, String> safeFields = DiagnosticLogSanitizer.sanitize(Map.of(
                "event", "diagnostics_checked", "status", snapshot.status()));
        LOGGER.atInfo()
                .addKeyValue("event", safeFields.get("event"))
                .addKeyValue("status", safeFields.get("status"))
                .log("Diagnostic dependency status checked");
        return new DiagnosticsResponse(snapshot.status(), snapshot.components(), DiagnosticContext.snapshot());
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:35
     */
    public record DiagnosticsResponse(String status,
                                      Map<String, DiagnosticComponentStatus> components,
                                      Map<String, String> context) {
    }
}
