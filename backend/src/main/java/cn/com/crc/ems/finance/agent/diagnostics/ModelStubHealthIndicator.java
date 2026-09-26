package cn.com.crc.ems.finance.agent.diagnostics;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;

/** Reports the intentionally explicit model stub boundary; it never calls a real model. */
@Component("modelStub")
public final class ModelStubHealthIndicator implements HealthIndicator, DiagnosticProbe {

    private final FinanceAgentProperties properties;

    @Autowired
    public ModelStubHealthIndicator(FinanceAgentProperties properties) {
        this.properties = properties;
    }

    @Override
    public String name() {
        return "modelStub";
    }

    @Override
    public DiagnosticComponentStatus check() {
        boolean stub = properties.getDeepSeek() != null && properties.getDeepSeek().isStub();
        return stub
                ? new DiagnosticComponentStatus(name(), "UP", "stub_enabled")
                : new DiagnosticComponentStatus(name(), "UNKNOWN", "real_model_probe_not_implemented");
    }

    @Override
    public Health health() {
        DiagnosticComponentStatus status = check();
        Health.Builder builder = switch (status.status()) {
            case "UP" -> Health.up();
            case "DOWN" -> Health.down();
            default -> Health.unknown();
        };
        return builder.withDetail("component", status.name()).withDetail("reason", status.reason()).build();
    }
}
