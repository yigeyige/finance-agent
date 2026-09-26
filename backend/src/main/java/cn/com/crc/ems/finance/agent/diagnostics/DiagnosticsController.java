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

/** Read-only, non-sensitive diagnostics endpoint. */
@RestController
@RequestMapping("/api/v1/diagnostics")
@ConditionalOnProperty(prefix = "finance-agent.diagnostics", name = "api-enabled", havingValue = "true")
public final class DiagnosticsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiagnosticsController.class);

    private final DiagnosticHealthService healthService;

    @Autowired
    public DiagnosticsController(DiagnosticHealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
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

    public record DiagnosticsResponse(String status,
                                      Map<String, DiagnosticComponentStatus> components,
                                      Map<String, String> context) {
    }
}
