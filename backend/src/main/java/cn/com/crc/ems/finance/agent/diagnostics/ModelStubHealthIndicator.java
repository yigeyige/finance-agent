package cn.com.crc.ems.finance.agent.diagnostics;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;

@Component("modelStub")
/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:36
 */
public final class ModelStubHealthIndicator implements HealthIndicator, DiagnosticProbe {

    private final FinanceAgentProperties properties;

    @Autowired
    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: properties 输入参数。
     * @author: yigeyige99
     */
    public ModelStubHealthIndicator(FinanceAgentProperties properties) {
        this.properties = properties;
    }

    @Override
    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public String name() {
        return "modelStub";
    }

    @Override
    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public DiagnosticComponentStatus check() {
        boolean stub = properties.getDeepSeek() != null && properties.getDeepSeek().isStub();
        return stub
                ? new DiagnosticComponentStatus(name(), "UP", "stub_enabled")
                : new DiagnosticComponentStatus(name(), "UNKNOWN", "real_model_probe_not_implemented");
    }

    @Override
    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
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
