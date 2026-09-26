package cn.com.crc.ems.finance.agent.diagnostics;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;

/** Reports the synthetic source adapter boundary without touching a real source system. */
@Component("sourceStub")
public final class SourceStubHealthIndicator implements HealthIndicator, DiagnosticProbe {

    private final FinanceAgentProperties properties;

    public SourceStubHealthIndicator(FinanceAgentProperties properties) {
        this.properties = properties;
    }

    @Override
    public String name() {
        return "sourceStub";
    }

    @Override
    public DiagnosticComponentStatus check() {
        FinanceAgentProperties.Source source = properties.getSource();
        boolean stub = source != null && source.isStub();
        return stub
                ? new DiagnosticComponentStatus(name(), "UP", "stub_enabled")
                : new DiagnosticComponentStatus(name(), "UNKNOWN", "real_source_probe_not_implemented");
    }

    @Override
    public Health health() {
        DiagnosticComponentStatus status = check();
        Health.Builder builder = "UP".equals(status.status()) ? Health.up() : Health.unknown();
        return builder.withDetail("component", status.name()).withDetail("reason", status.reason()).build();
    }
}
