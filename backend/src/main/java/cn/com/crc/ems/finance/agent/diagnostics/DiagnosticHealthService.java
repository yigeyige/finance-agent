package cn.com.crc.ems.finance.agent.diagnostics;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/** Collects safe dependency statuses for the read-only diagnostics endpoint. */
@Service
public final class DiagnosticHealthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiagnosticHealthService.class);

    private final List<DiagnosticProbe> probes;

    @Autowired
    public DiagnosticHealthService(List<DiagnosticProbe> probes) {
        this.probes = probes.stream().sorted(Comparator.comparing(DiagnosticProbe::name)).toList();
    }

    public DiagnosticSnapshot snapshot() {
        Map<String, DiagnosticComponentStatus> components = new LinkedHashMap<>();
        for (DiagnosticProbe probe : probes) {
            DiagnosticComponentStatus status;
            try {
                status = probe.check();
            } catch (RuntimeException exception) {
                DiagnosticFailureTelemetry.log(LOGGER, probe.name(), exception);
                status = new DiagnosticComponentStatus(probe.name(), "DOWN", "probe_failed");
            }
            components.put(probe.name(), status);
        }
        String overall = components.values().stream().anyMatch(status -> "DOWN".equals(status.status()))
                ? "DOWN"
                : components.values().stream().anyMatch(status -> "UNKNOWN".equals(status.status()))
                        ? "UNKNOWN" : "UP";
        return new DiagnosticSnapshot(overall, components);
    }

    public record DiagnosticSnapshot(String status, Map<String, DiagnosticComponentStatus> components) {
    }
}
