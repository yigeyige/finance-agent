package cn.com.crc.ems.finance.agent.diagnostics;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class DiagnosticLogFilterTest {

    private final DiagnosticLogFilter filter = new DiagnosticLogFilter();
    private final Logger logger = (Logger) LoggerFactory.getLogger("diagnostic-test");

    @Test
    void deniesSensitiveMessageAndAllowsSafeMessage() {
        assertThat(filter.decide(null, logger, Level.INFO, "password=%s",
                new Object[]{"secret-value"}, null)).isEqualTo(FilterReply.DENY);
        assertThat(filter.decide(null, logger, Level.INFO, "component=%s status=%s",
                new Object[]{"database", "UP"}, null)).isEqualTo(FilterReply.NEUTRAL);
    }
}
