package cn.com.crc.ems.finance.agent.diagnostics;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:37
 */
@Component("sourceStub")
public final class SourceStubHealthIndicator implements HealthIndicator, DiagnosticProbe {

    private final FinanceAgentProperties properties;

    @Autowired
    public SourceStubHealthIndicator(FinanceAgentProperties properties) {
        this.properties = properties;
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    @Override
    public String name() {
        return "sourceStub";
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    @Override
    public DiagnosticComponentStatus check() {
        FinanceAgentProperties.Source source = properties.getSource();
        boolean stub = source != null && source.isStub();
        return stub
                ? new DiagnosticComponentStatus(name(), "UP", "stub_enabled")
                : new DiagnosticComponentStatus(name(), "UNKNOWN", "real_source_probe_not_implemented");
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    @Override
    public Health health() {
        DiagnosticComponentStatus status = check();
        Health.Builder builder = "UP".equals(status.status()) ? Health.up() : Health.unknown();
        return builder.withDetail("component", status.name()).withDetail("reason", status.reason()).build();
    }
}
